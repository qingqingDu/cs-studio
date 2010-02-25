package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;

/** A Channel with thread-safe reference count.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class RefCountedChannel
{
    private Channel channel;

    private int refs;

    /** Initialize
     *  @param channel ChannelAccess channel
     *  @throws Error when channel is <code>null</code>
     */
    public RefCountedChannel(final Channel channel)
    {
        if (channel == null)
            throw new Error("Channel must not be null");
        this.channel = channel;
        refs = 1;
    }

    /** Increment reference count */
    synchronized public void incRefs()
    {   ++refs;  }

    /** Decrement reference count.
     *  @return Remaining references.
     */
    synchronized public int decRefs()
    {
        --refs;
        return refs;
    }

    /** @return ChannelAccess channel */
    public Channel getChannel()
    {   return channel;   }

    /** Must be called when all references are gone
     *  @throws Error when channel is still references
     */
    public void dispose()
    {
        if (refs != 0)
            throw new Error("Channel destroyed while referenced " + refs + " times");
        try
        {
            channel.destroy();
        }
        catch (Exception ex)
        {
        	Activator.getLogger().warn("Channel.destroy failed", ex); //$NON-NLS-1$
        }
        channel = null;
    }
}