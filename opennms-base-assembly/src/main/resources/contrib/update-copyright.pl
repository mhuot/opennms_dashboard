#!/usr/bin/perl

use Fcntl ':mode';
use File::Find;
use File::Copy;

my $debug = 0;

my $header = "This file is part of OpenNMS(R).

Copyright (C) ###datestring### The OpenNMS Group, Inc.
OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.

OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.

OpenNMS(R) is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published
by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

OpenNMS(R) is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with OpenNMS(R).  If not, see:
     http://www.gnu.org/licenses/

For more information contact:
    OpenNMS(R) Licensing <license\@opennms.org>
    http://www.opennms.org/
    http://www.opennms.com/";

sub write_jsp {
	my $dates = shift;
	print FILEOUT "<%--\n";
	write_java($dates);
	print FILEOUT "--%>\n";
}

sub write_java {
	my $dates = shift;
	print FILEOUT "/*******************************************************************************\n";
	for my $line (split(/\n/, $header)) {
		if ($line eq "") {
			print FILEOUT " *\n";
		} else {
			$line =~ s/###datestring###/$dates/g;
			print FILEOUT " * ", $line, "\n";
		}
	}
	print FILEOUT " *******************************************************************************/\n\n";
}

sub write_properties {
	my $dates = shift;
	print FILEOUT "###############################################################################\n";
	for my $line (split(/\n/, $header)) {
		if ($line eq "") {
			print FILEOUT "#\n";
		} else {
			$line =~ s/###datestring###/$dates/g;
			print FILEOUT "# ", $line, "\n";
		}
	}
	print FILEOUT "###############################################################################\n\n";
}

die "usage: $0 <directory>" if (@ARGV == 0);

my @directories = ();

for my $entry (@ARGV) {
	if (-d $entry) {
		push(@directories, $entry);
	} elsif (-f $entry) {
		process_file($entry);
	} else {
		print STDERR "! warning: $entry does not exist! ($!)\n";
	}
}

find({
	wanted => sub {
		process_file($File::Find::name);
	},
	no_chdir => 1
}, @directories) unless (@directories == 0);

sub process_file {
	my $name = shift;

	return if ($name =~ /\/target\//);
	return if ($name =~ /\/\.git\//);
	return unless (-f $name);
	return unless ($name =~ /\.(jsp|java|properties)$/);
	return if ($name =~ /\/test\/.*\.properties$/);
	return if ($name =~ /\/castor.properties$/);

	print "* $name\n";

	my $begin_date = 2011;
	my $end_date   = 2011;

	open (GIT, "git log --date=short '$name' |") or die "unable to read from 'git log --date=short $name': $!\n";
	while (my $line = <GIT>) {
		if ($line =~ /^\s*Date:\s+(\d+)/) {
			my $date = $1;
			if ($date < $begin_date) {
				$begin_date = $date;
			} elsif ($date > $end_date) {
				$end_date = $date;
			}
		}
	}
	close (GIT);

	my $datestring = "$begin_date-$end_date";
	if ($begin_date == $end_date) {
		$datestring = "$begin_date";
	}

	open (FILEIN, "$name") or die "unable to read from $name: $!\n";
	open (FILEOUT, ">$name.tmp.$$") or die "unable to write to $name.tmp.$$: $!\n";

	my (undef, undef, $mode) = lstat($name);

	my ($extension) = $name =~ /.*\.(.*?)$/;
	my $found = 0;
	my $in_header = undef;
	my $in_comment = undef;
	my $comment_contents = "";

	LOOP: while (my $line = <FILEIN>) {
		print "  $line" if ($debug);

		print "1. in_comment = $in_comment\n" if ($debug);
		print "2. in_header  = $in_header\n"  if ($debug);
		if (defined $in_comment) {
			my $doit = 0;
			if ($in_comment eq "/*") {
				if ($line =~ /\*\//) {
					$doit = 1;
				}
			} elsif ($extension eq "jsp" and $in_comment eq "<%--") {
				if ($line =~ /\-\-\%\>/) {
					$doit = 1;
				}
			} elsif ($extension eq "jsp" and $in_comment eq "<!--") {
				if ($line =~ /\-\-\!?\>/) {
					$doit = 1;
				}
			} elsif ($extension eq "properties" and $in_comment eq "#") {
				if (($line !~ /^\s*#/ and $line !~ /^\s*$/) or ($line =~ /^\s*\#\#\#\#\#\#\#\#\#\#\#*\s*$/)) {
					$doit = 1;
				}
			} elsif ($in_comment eq "//") {
				if ($line !~ /^\s*\/\// and $line !~ /^\s*$/) {
					$doit = 1;
				}
			}

			if ($doit) {
				if (defined $in_header) {
					if ($in_comment eq "//") {
						if ($extension eq "jsp") {
							write_java($datestring);
						} else {
							&{"write_$extension"}($datestring);
						}
						print FILEOUT $line;
					} elsif ($in_comment eq "#" and $line !~ /^\s*\#\#\#\#\#\#\#\#\#\#\#*\s*$/) {
						&{"write_$extension"}($datestring);
						print FILEOUT $line;
					} else {
						&{"write_$extension"}($datestring);
					}
				} else {
					$comment_contents .= $line;
					print FILEOUT $comment_contents;
				}
				$in_comment = undef;
				$in_header = undef;
				$comment_contents = undef;
				next LOOP;
			}
		}
		
		print "3. in_comment = $in_comment\n" if ($debug);
		print "4. in_header = $in_header\n"   if ($debug);
		if (not defined $in_comment) {
			if ($line =~ /^\s*(\/\*|\/\/)/) {
				$in_comment = $1;
			} elsif ($line =~ /\s*(\/\*|\/\/)\s*$/) {
				$in_comment = $1;
			} elsif ($extension eq "jsp" and $line =~ /(\<\!\-\-)/) {
				$in_comment = $1;
			} elsif ($extension eq "jsp" and $line =~ /(\<\%\-\-)/) {
				$in_comment = $1;
			} elsif ($extension eq "properties" and $line =~ /^\s*(\#)/) {
				$in_comment = $1;
			}

			if ($in_comment eq "/*" and $line =~ /\*\//) {
				$in_comment = undef;
			}
			if ($extension eq "jsp" and $in_comment eq "<!--" and $line =~ /\-\-\!?\>/) {
				$in_comment = undef;
			}
			if ($extension eq "jsp" and $in_comment eq "<%--" and $line =~ /\-\-\%\>/) {
				$in_comment = undef;
			}
		}

		print "5. in_comment = $in_comment\n" if ($debug);
		print "6. in_header = $in_header\n"   if ($debug);
		if (not $found and not defined $in_header) {
			if (defined $in_comment and ($line =~ /This file is (a )?part of (the )?OpenNMS/i or $line =~ /The OpenNMS Project Contributor Agreement/)) {
				$in_header = $in_comment;
				$found = 1;
			}
		}

		print "7. in_comment = $in_comment\n" if ($debug);
		print "8. in_header = $in_header\n"   if ($debug);
		if ($in_comment) {
			$comment_contents .= $line;
		} else {
			print FILEOUT $line;
		}
	}

	close (FILEOUT);
	close (FILEIN);

	move("$name.tmp.$$", "$name");
	chmod($mode, $name);

	if (not $found) {
		open (FILEIN, "$name") or die "unable to read from $name: $!\n";
		open (FILEOUT, ">$name.tmp.$$") or die "unable to write to $name.tmp.$$: $!\n";

		&{"write_$extension"}($datestring);

		while (my $line = <FILEIN>) {
			print FILEOUT $line;
		}

		close(FILEOUT);
		close(FILEIN);

		move("$name.tmp.$$", "$name");
		chmod($mode, $name);
	}
}
