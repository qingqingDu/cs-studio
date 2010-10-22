/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.model.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;


/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 27.07.2010
 */
public class ProcessVariable2IONameImplemation implements ProcessVariable2IONameService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIOName(final String pvName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INode getNode(final String pvName) {
        final ArrayList<String> list = new ArrayList<String>();
        list.add(pvName);
        final List<PV2IONameMatcherModelDBO> pv2ioNameMatchers = getPV2IONameMatchers(list);
        if(pv2ioNameMatchers!=null) {
            for (final PV2IONameMatcherModelDBO pv2ioNameMatcher : pv2ioNameMatchers) {
                final ChannelDBO value = Repository.loadChannel(pv2ioNameMatcher.getIoName());
                return value;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map<String, INode> getNodes(final Collection<String> pvName) {
        final Map<String, INode> nodes = new HashMap<String, INode>();
        final List<PV2IONameMatcherModelDBO> pv2ioNameMatchers = getPV2IONameMatchers(pvName);
        if(pv2ioNameMatchers!=null) {
            for (final PV2IONameMatcherModelDBO pv2ioNameMatcher : pv2ioNameMatchers) {
                final ChannelDBO value = Repository.loadChannel(pv2ioNameMatcher.getIoName());
                nodes.put(pv2ioNameMatcher.getEpicsName(), value);
            }
        }
        return nodes;
    }

    @CheckForNull
    private PV2IONameMatcherModelDBO getPV2IONameMatcher(final String pvName){
        final ArrayList<String > l = new ArrayList<String>();
        l.add(pvName);
        final List<PV2IONameMatcherModelDBO> matchers = Repository.loadPV2IONameMatcher(l);
        if(matchers.size()>1) {
            throw new IllegalStateException("Found more then one IOName for the pvName: "+pvName);
        }
        if(!matchers.isEmpty()) {
            return matchers.get(0);
        }
         return null;
    }
    @CheckForNull
    private List<PV2IONameMatcherModelDBO> getPV2IONameMatchers(final Collection<String> pvName){
        final List<PV2IONameMatcherModelDBO> matchers = Repository.loadPV2IONameMatcher(pvName);
        return matchers;
    }

}
