/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.jicmp.jna;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;

/**
 * UnixNativeSocketFactory
 *
 * @author brozow
 */
public class Win32V4NativeSocket extends NativeDatagramSocket {
    
    static {
        Native.register((String)null);
    }

    private int m_sock;     

    public Win32V4NativeSocket(int family, int type, int protocol) throws Exception {
        m_sock = socket(family, type, protocol);
    }
    
    public native int socket(int domain, int type, int protocol) throws LastErrorException;

    public native int sendto(int socket, Buffer buffer, int buflen, int flags, sockaddr_in dest_addr, int dest_addr_len) throws LastErrorException;

    public native int recvfrom(int socket, Buffer buffer, int buflen, int flags, sockaddr_in in_addr, int[] in_addr_len) throws LastErrorException;

    public native int closesocket(int socket) throws LastErrorException;

    public int close(int socket) throws LastErrorException {
        return closesocket(socket);
    }

    private int getSock() {
        return m_sock;
    }

    @Override
    public int receive(NativeDatagramPacket p) {
        sockaddr_in in_addr = new sockaddr_in();
        int[] szRef = new int[] { in_addr.size() };
        
        ByteBuffer buf = p.getContent();
        
        int n = recvfrom(getSock(), buf, buf.capacity(), 0, in_addr, szRef);
        p.setLength(n);
        p.setAddress(in_addr.getAddress());
        p.setPort(in_addr.getPort());
        
        return n;
    }

    @Override
    public int send(NativeDatagramPacket p) {
        sockaddr_in destAddr = new sockaddr_in(p.getAddress(), p.getPort());
        ByteBuffer buf = p.getContent();
        return sendto(getSock(), buf, buf.remaining(), 0, destAddr, destAddr.size());
    }

    @Override
    public int close() {
        return close(getSock());
    }

}
