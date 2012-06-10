/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2011 The OpenNMS Group, Inc.
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

package org.opennms.web.alarm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.opennms.core.utils.BeanUtils;
import org.opennms.core.utils.LogUtils;
import org.opennms.netmgt.model.OnmsSeverity;
import org.opennms.netmgt.model.TroubleTicketState;
import org.opennms.web.alarm.filter.AlarmCriteria;
import org.opennms.web.alarm.filter.AlarmIdFilter;
import org.opennms.web.alarm.filter.AlarmIdListFilter;
import org.opennms.web.alarm.filter.AlarmTypeFilter;
import org.opennms.web.alarm.filter.SeverityBetweenFilter;
import org.opennms.web.alarm.filter.SeverityFilter;
import org.opennms.web.alarm.filter.AlarmCriteria.AlarmCriteriaVisitor;
import org.opennms.web.alarm.filter.AlarmCriteria.BaseAlarmCriteriaVisitor;
import org.opennms.web.filter.AndFilter;
import org.opennms.web.filter.ConditionalFilter;
import org.opennms.web.filter.Filter;
import org.opennms.web.filter.OrFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * <p>JdbcWebAlarmRepository class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
@Deprecated
public class JdbcWebAlarmRepository implements WebAlarmRepository, InitializingBean {
    
    @Autowired
    SimpleJdbcTemplate m_simpleJdbcTemplate;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.assertAutowiring(this);
    }

    private String getSql(final String selectClause, final AlarmCriteria criteria) {
        final StringBuilder buf = new StringBuilder(selectClause);

        criteria.visit(new AlarmCriteriaVisitor<RuntimeException>() {

            boolean first = true;

            public void and(StringBuilder buf) {
                if (first) {
                    buf.append(" WHERE ");
                    first = false;
                } else {
                    buf.append(" AND ");
                }
            }

            public void visitAckType(AcknowledgeType ackType) {
                and(buf);
                buf.append(ackType.getAcknowledgeTypeClause());
            }

            public void visitFilter(Filter filter) {
                and(buf);
                buf.append(filter.getParamSql());
            }

            public void visitSortStyle(SortStyle sortStyle) {
                buf.append(" ");
                buf.append(sortStyle.getOrderByClause());
            }

            public void visitLimit(int limit, int offset) {
                buf.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
            } 

        });

        return buf.toString();
    }
    
    private PreparedStatementSetter paramSetter(final AlarmCriteria criteria, final Object... args) {
        return new PreparedStatementSetter() {
            int paramIndex = 1;
            public void setValues(final PreparedStatement ps) throws SQLException {
                for(Object arg : args) {
                    ps.setObject(paramIndex, arg);
                    paramIndex++;
                }
                criteria.visit(new BaseAlarmCriteriaVisitor<SQLException>() {
                    @Override
                    public void visitFilter(Filter filter) throws SQLException {
                        paramIndex += filter.bindParam(ps, paramIndex);
                    }
                });
            }
        };
    }

    @Override
    public void updateStickyMemo(Integer alarmId, String body, String user) {
        throw new UnsupportedOperationException("Not supported yet. JdbcWebAlarmRepositony is deprecated.");
    }

    @Override
    public void updateReductionKeyMemo(Integer alarmId, String body, String user) {
        throw new UnsupportedOperationException("Not supported yet. JdbcWebAlarmRepositony is deprecated.");
    }

    @Override
    public void removeStickyMemo(Integer alarmId) {
        throw new UnsupportedOperationException("Not supported yet. JdbcWebAlarmRepositony is deprecated.");
    }

    @Override
    public void removeReductionKeyMemo(int alarmId) {
        throw new UnsupportedOperationException("Not supported yet. JdbcWebAlarmRepositony is deprecated.");
    }
    
    private static class AlarmMapper implements ParameterizedRowMapper<Alarm> {
        public Alarm mapRow(ResultSet rs, int rowNum) throws SQLException {
            Alarm alarm = new Alarm();
            alarm.id = rs.getInt("alarmID");
            alarm.uei = rs.getString("eventUei");
            alarm.dpName = rs.getString("dpName");

            // node id can be null, in which case nodeID will be 0
            alarm.nodeID = new Integer(rs.getInt("nodeID"));
            alarm.ipAddr = rs.getString("ipAddr");

            // This causes serviceID to be null if the column in the database is null
            alarm.serviceID = ((Integer) rs.getObject("serviceID"));
            alarm.reductionKey = rs.getString("reductionKey");
            alarm.count = rs.getInt("counter");
            alarm.severity = OnmsSeverity.get(rs.getInt("severity"));
            alarm.lastEventID = rs.getInt("lastEventID");
            alarm.firsteventtime = getTimestamp("firsteventtime", rs);
            alarm.lasteventtime = getTimestamp("lasteventtime", rs);
            alarm.description = rs.getString("description");
            alarm.logMessage = rs.getString("logmsg");
            alarm.operatorInstruction = rs.getString("OperInstruct");
            alarm.troubleTicket = rs.getString("TTicketID");
            
            Integer stateCode = (Integer) rs.getObject("TTicketState");
            for (TroubleTicketState state : TroubleTicketState.values()) {
                if (stateCode != null && state.ordinal() == stateCode.intValue()) {
                    alarm.troubleTicketState = state;
                }
            }

            alarm.mouseOverText = rs.getString("MouseOverText");
            alarm.suppressedUntil = getTimestamp("suppressedUntil", rs);
            alarm.suppressedUser = rs.getString("suppressedUser");
            alarm.suppressedTime = getTimestamp("suppressedTime", rs);
            alarm.acknowledgeUser = rs.getString("alarmAckUser");
            alarm.acknowledgeTime = getTimestamp("alarmAckTime", rs);

            alarm.nodeLabel = rs.getString("nodeLabel");
            alarm.serviceName = rs.getString("serviceName");
            
            return alarm;
            
        }
        
        private Date getTimestamp(String field, ResultSet rs) throws SQLException{
            if(rs.getTimestamp(field) != null){
                return new Date(rs.getTimestamp(field).getTime());
            }else{
                return null;
            }
        }
    }

    /** {@inheritDoc} */
    public int countMatchingAlarms(AlarmCriteria criteria) {
        String sql = getSql("SELECT COUNT(ALARMID) as ALARMCOUNT FROM ALARMS LEFT OUTER JOIN NODE USING (NODEID) LEFT OUTER JOIN SERVICE USING (SERVICEID) ", criteria);
        return queryForInt(sql, paramSetter(criteria));
    }
    
    /** {@inheritDoc} */
    public int[] countMatchingAlarmsBySeverity(AlarmCriteria criteria) {
        String selectClause = "SELECT SEVERITY, COUNT(ALARMID) AS ALARMCOUNT FROM ALARMS LEFT OUTER JOIN NODE USING (NODEID) LEFT OUTER JOIN SERVICE USING (SERVICEID) ";
        String sql = getSql(selectClause, criteria);
        sql = sql + " GROUP BY SEVERITY";

        final int[] alarmCounts = new int[8];
        jdbc().query(sql, paramSetter(criteria), new RowCallbackHandler() {

            public void processRow(ResultSet rs) throws SQLException {
                int severity = rs.getInt("SEVERITY");
                int alarmCount = rs.getInt("ALARMCOUNT");

                alarmCounts[severity] = alarmCount;

            }
            
        });
        
        return alarmCounts;
    }
    
    /** {@inheritDoc} */
    public Alarm getAlarm(int alarmId) {
        Alarm[] alarms = getMatchingAlarms(new AlarmCriteria(new AlarmIdFilter(alarmId)));
        if (alarms.length < 1) {
            return null;
        } else {
            return alarms[0];
        }
    }
    
    /** {@inheritDoc} */
    public Alarm[] getMatchingAlarms(AlarmCriteria criteria) {
        String sql = getSql("SELECT ALARMS.*, NODE.NODELABEL, SERVICE.SERVICENAME FROM ALARMS LEFT OUTER JOIN NODE USING (NODEID) LEFT OUTER JOIN SERVICE USING (SERVICEID) ", criteria);
        return getAlarms(sql, paramSetter(criteria));
    }
    

    private Alarm[] getAlarms(String sql, PreparedStatementSetter setter) {
        List<Alarm> alarms = queryForList(sql, setter, new AlarmMapper());
        return alarms.toArray(new Alarm[0]);
    }

    
    /**
     * <p>acknowledgeAlarms</p>
     *
     * @param alarmIds an array of int.
     * @param user a {@link java.lang.String} object.
     * @param timestamp a java$util$Date object.
     */
    public void acknowledgeAlarms(int[] alarmIds, String user, Date timestamp) {
        acknowledgeMatchingAlarms(user, timestamp, new AlarmCriteria(new AlarmIdListFilter(alarmIds)));
    }

    /** {@inheritDoc} */
    public void acknowledgeMatchingAlarms(String user, Date timestamp, AlarmCriteria criteria) {
        String sql = getSql("UPDATE ALARMS SET ALARMACKUSER=?, ALARMACKTIME=? ", criteria);
        jdbc().update(sql, paramSetter(criteria, user, new Timestamp(timestamp.getTime())));
    }
    
    /**
     * <p>acknowledgeAll</p>
     *
     * @param user a {@link java.lang.String} object.
     * @param timestamp a java$util$Date object.
     */
    public void acknowledgeAll(String user, Date timestamp) {
        m_simpleJdbcTemplate.update("UPDATE ALARMS SET ALARMACKUSER=?, ALARMACKTIME=? WHERE ALARMACKUSER IS NULL ", user, new Timestamp(timestamp.getTime()));
    }

    /** {@inheritDoc} */
    public void unacknowledgeAlarms(int[] alarmIds, String user) {
        unacknowledgeMatchingAlarms(new AlarmCriteria(new AlarmIdListFilter(alarmIds)), user);
    }

    /** {@inheritDoc} */
    public void unacknowledgeMatchingAlarms(AlarmCriteria criteria, String user) {
        String sql = getSql("UPDATE ALARMS SET ALARMACKUSER=NULL, ALARMACKTIME=NULL ", criteria);
        jdbc().update(sql, paramSetter(criteria));
    }
    
    /** {@inheritDoc} */
    public void unacknowledgeAll(String user) {
        m_simpleJdbcTemplate.update("UPDATE ALARMS SET ALARMACKUSER=NULL, ALARMACKTIME=NULL WHERE ALARMACKUSER IS NOT NULL ");
    }
    
    /**
     * <p>clearAlarms</p>
     *
     * @param alarmIds an array of int.
     * @param user a {@link java.lang.String} object.
     */
    public void clearAlarms(int[] alarmIds, String user){
        clearAlarms(alarmIds, user, new Date());
    }
    
    /**
     * <p>clearAlarms</p>
     *
     * @param alarmIds an array of int.
     * @param user a {@link java.lang.String} object.
     * @param timestamp a java$util$Date object.
     */
    public void clearAlarms(int[] alarmIds, String user, Date timestamp) {
        if(alarmIds == null || user == null || timestamp == null){
            throw new IllegalArgumentException("Cannot take null parameters");
        }
        
        AlarmCriteria criteria = new AlarmCriteria(new AlarmIdListFilter(alarmIds), new SeverityBetweenFilter(OnmsSeverity.NORMAL, OnmsSeverity.CRITICAL));
        
        String sql = getSql("UPDATE ALARMS SET SEVERITY =?, ALARMTYPE =? ", criteria);
        LogUtils.infof(this, sql);
        jdbc().update(sql, paramSetter(criteria, OnmsSeverity.CLEARED.getId(), Alarm.RESOLUTION_TYPE));
        
    }
    
    /**
     * <p>escalateAlarms</p>
     *
     * @param alarmIds an array of int.
     * @param user a {@link java.lang.String} object.
     * @param timestamp a java$util$Date object.
     */
    public void escalateAlarms(int[] alarmIds, String user, Date timestamp) {
        ConditionalFilter condFilter = new AndFilter(new AlarmTypeFilter(Alarm.PROBLEM_TYPE), new SeverityFilter(OnmsSeverity.CLEARED));
        ConditionalFilter condFilter2 = new AndFilter(new AlarmTypeFilter(Alarm.PROBLEM_TYPE), new SeverityBetweenFilter(OnmsSeverity.CLEARED, OnmsSeverity.CRITICAL));
        ConditionalFilter orCondFilter = new OrFilter(condFilter, condFilter2);
        
        AlarmCriteria criteria = new AlarmCriteria(new AlarmIdListFilter(alarmIds), orCondFilter);
        
        String sql = getSql("UPDATE ALARMS SET SEVERITY = ( CASE WHEN SEVERITY =? THEN ? ELSE ( CASE WHEN SEVERITY <? THEN SEVERITY + 1 ELSE ? END) END), ALARMTYPE =? ", criteria);
        LogUtils.infof(this, sql);
        jdbc().update(sql, paramSetter(criteria, OnmsSeverity.CLEARED.getId(), OnmsSeverity.WARNING.getId(), OnmsSeverity.CRITICAL.getId(), OnmsSeverity.CRITICAL.getId(), Alarm.PROBLEM_TYPE));
    }
    
    private int queryForInt(String sql, PreparedStatementSetter setter) throws DataAccessException {
        Integer number = queryForObject(sql, setter, new SingleColumnRowMapper<Integer>(Integer.class));
        return (number != null ? number.intValue() : 0);
    }
    
    private <T> T queryForObject(String sql, PreparedStatementSetter setter, RowMapper<T> rowMapper) throws DataAccessException {
        return DataAccessUtils.requiredSingleResult(jdbc().query(sql, setter, new RowMapperResultSetExtractor<T>(rowMapper, 1)));
    }


    private <T> List<T> queryForList(String sql, PreparedStatementSetter setter, ParameterizedRowMapper<T> rm) {
        return jdbc().query(sql, setter, new RowMapperResultSetExtractor<T>(rm));
    }
    
    private JdbcOperations jdbc() {
        return m_simpleJdbcTemplate.getJdbcOperations();
    }

}
