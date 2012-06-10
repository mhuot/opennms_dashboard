package org.opennms.netmgt.syslogd;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennms.core.utils.LogUtils;

public class JuniperSyslogParser extends SyslogParser {
    //                                                                PRI         TIMESTAMP                                          HOST      PROCESS/ID          MESSAGE
    private static final Pattern m_juniperPattern = Pattern.compile("^<(\\d+)>\\s*(\\S\\S\\S\\s+\\d{1,2}\\s+\\d\\d:\\d\\d:\\d\\d)\\s+(\\S+)\\s+(\\S+)\\[(\\d+)\\]: (.*?)$", Pattern.MULTILINE);

    protected JuniperSyslogParser(final String text) {
        super(text);
    }

    public static SyslogParser getParser(final String text) {
        return new JuniperSyslogParser(text);
    }
    
    protected Pattern getPattern() {
        return m_juniperPattern;
    }
    
    public SyslogMessage parse() throws SyslogParserException {
        if (!this.find()) {
            if (traceEnabled()) {
                LogUtils.tracef(this, "'%s' did not match '%s', falling back to the custom parser", m_juniperPattern, getText());
                final SyslogParser custom = CustomSyslogParser.getParser(getText());
                return custom.parse();
            }
            return null;
        }

        final Matcher matcher = getMatcher();
        final SyslogMessage message = new SyslogMessage();

        try {
            final int priorityField = Integer.parseInt(matcher.group(1));
            message.setFacility(SyslogFacility.getFacilityForCode(priorityField));
            message.setSeverity(SyslogSeverity.getSeverityForCode(priorityField));
        } catch (final NumberFormatException nfe) {
            LogUtils.debugf(this, nfe, "Unable to parse '%s' as a PRI code.", matcher.group(1));
        }
        Date date = parseDate(matcher.group(2));
        if (date == null) date = new Date();
        message.setDate(date);

        message.setHostName(matcher.group(3));
        message.setProcessName(matcher.group(4));
        try {
            final Integer pid = Integer.parseInt(matcher.group(5));
            message.setProcessId(pid);
        } catch (final NumberFormatException nfe) {
            LogUtils.debugf(this, nfe, "Unable to parse '%s' as a process ID.", matcher.group(5));
        }
        message.setMessage(matcher.group(6).trim());

        return message;
    }


}
