/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl.severity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.common.service.severity.ArchiveSeverity;
import org.csstudio.archive.common.service.severity.ArchiveSeverityId;
import org.csstudio.archive.common.service.severity.IArchiveSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;

import com.google.common.collect.Maps;

/**
 * Archive severity dao implementation.
 *
 * @author bknerr
 * @since 19.11.2010
 */
public class ArchiveSeverityDaoImpl extends AbstractArchiveDao implements IArchiveSeverityDao {

    private static final String RETRIEVAL_FAILED = "Severity retrieval from archive failed.";

    /**
     * Archive severity cache.
     */
    private final Map<EpicsAlarmSeverity, IArchiveSeverity> _severityCacheByEnum = Maps.newEnumMap(EpicsAlarmSeverity.class);
    private final Map<ArchiveSeverityId, IArchiveSeverity> _severityCacheById = Maps.newHashMap();

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectSeverityByNameStmt =
        "SELECT id FROM archive.severity WHERE name=?";
    private final String _selectSeverityByIdStmt =
        "SELECT name FROM archive.severity WHERE id=?";

    /**
     * Constructor.
     */
    public ArchiveSeverityDaoImpl(@Nonnull final ArchiveDaoManager mgr) {
        super(mgr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ArchiveSeverityId retrieveSeverityId(@Nonnull final EpicsAlarmSeverity sev) throws ArchiveDaoException {

        final IArchiveSeverity severity = retrieveSeverity(sev);
        if (severity != null) {
            return severity.getId();
        }
        return null;
    }

    @Override
    @CheckForNull
    public IArchiveSeverity retrieveSeverity(@Nonnull final EpicsAlarmSeverity sev) throws ArchiveDaoException {

        final IArchiveSeverity severity = _severityCacheByEnum.get(sev);
        if (severity != null) {
            return severity;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectSeverityByNameStmt);
            stmt.setString(1, sev.name());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final ArchiveSeverityId id = new ArchiveSeverityId(result.getInt(1));
                final IArchiveSeverity newSev = new ArchiveSeverity(id, sev.name());

                _severityCacheByEnum.put(sev, newSev);
                _severityCacheById.put(id, newSev);
                return newSev;
            }
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectSeverityByNameStmt + " failed.");
        }
        return null;
    }

    @Override
    @CheckForNull
    public IArchiveSeverity retrieveSeverityById(@Nonnull final ArchiveSeverityId id) throws ArchiveDaoException {

        final IArchiveSeverity severity = _severityCacheById.get(id);
        if (severity != null) {
            return severity;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectSeverityByIdStmt);
            stmt.setLong(1, id.longValue());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final String name = result.getString(1);
                final IArchiveSeverity newSev = new ArchiveSeverity(id, name);

                _severityCacheById.put(id, newSev);
                _severityCacheByEnum.put(EpicsAlarmSeverity.parseSeverity(name), newSev);

                return newSev;
            }
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectSeverityByIdStmt + " failed.");
        }
        return null;
    }


}
