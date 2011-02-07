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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link SampleAggregator}.
 *
 * @author bknerr
 * @since 20.12.2010
 */
public class SampleAggregatorTest {

    private static EpicsAlarm ALARM = new EpicsAlarm(EpicsAlarmSeverity.INVALID, EpicsAlarmStatus.BADSUB);

    @Test
    public void initTest() {
        final TimeInstant ts = TimeInstantBuilder.buildFromNow();
        final SampleAggregator agg = new SampleAggregator(1.0, null, ts);

        Assert.assertEquals(Double.valueOf(1.0), agg.getAvg());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMax());
        Assert.assertNull(agg.getHighestAlarm());
        Assert.assertEquals(ts, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());
    }

    @Test
    public void aggregateTest() {
        final SampleAggregator agg = new SampleAggregator(1.0, null, TimeInstantBuilder.buildFromNow());

        final TimeInstant ts = TimeInstantBuilder.buildFromNow();
        agg.aggregateNewVal(2.0, ALARM, ts);

        Assert.assertEquals(Double.valueOf(1.5), agg.getAvg());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(2.0), agg.getMax());
        Assert.assertNull(agg.getHighestAlarm());
        Assert.assertEquals(ts, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());


        final TimeInstant ts3 = TimeInstantBuilder.buildFromNow();
        agg.aggregateNewVal(3.0, ALARM, -1.0, 20.0, ts3);
        Assert.assertEquals(Double.valueOf(2.0), agg.getAvg());
        Assert.assertEquals(Double.valueOf(-1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(20.0), agg.getMax());
        Assert.assertNull(agg.getHighestAlarm());
        Assert.assertEquals(ts3, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());

        agg.reset();
        Assert.assertNull(agg.getAvg());
        Assert.assertNull(agg.getMin());
        Assert.assertNull(agg.getMax());
        Assert.assertNull(agg.getHighestAlarm());
        Assert.assertNull(agg.getSampleTimestamp());
        Assert.assertEquals(Double.valueOf(2.0), agg.getAverageBeforeReset());
        Assert.assertEquals(ts3, agg.getResetTimestamp());
    }

    @Test
    public void aggregateAlarmTest() {
        final SampleAggregator agg = new SampleAggregator(1.0,
                                                          new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM,
                                                                         EpicsAlarmStatus.BADSUB),
                                                          TimeInstantBuilder.buildFromNow());

        final EpicsAlarm minor = new EpicsAlarm(EpicsAlarmSeverity.MINOR,
                                          EpicsAlarmStatus.BADSUB);
        agg.aggregateNewVal(2.0,
                            minor,
                            TimeInstantBuilder.buildFromNow());
        agg.aggregateNewVal(2.0,
                            new EpicsAlarm(EpicsAlarmSeverity.UNKNOWN,
                                           EpicsAlarmStatus.BADSUB),
                                           TimeInstantBuilder.buildFromNow());
        Assert.assertTrue(minor.equals(agg.getHighestAlarm()));

        final EpicsAlarm major = new EpicsAlarm(EpicsAlarmSeverity.MAJOR,
                                                EpicsAlarmStatus.BADSUB);
        agg.aggregateNewVal(2.0,
                            major,
                            TimeInstantBuilder.buildFromNow());
        Assert.assertTrue(major.equals(agg.getHighestAlarm()));

        final EpicsAlarm invalid = new EpicsAlarm(EpicsAlarmSeverity.INVALID,
                                                  EpicsAlarmStatus.BADSUB);
        agg.aggregateNewVal(2.0,
                            invalid,
                            TimeInstantBuilder.buildFromNow());
        Assert.assertTrue(invalid.equals(agg.getHighestAlarm()));
    }

}
