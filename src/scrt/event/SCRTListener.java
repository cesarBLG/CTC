package scrt.event;

import java.util.EventListener;

public interface SCRTListener extends EventListener
{
	public void actionPerformed(SRCTEvent e);
	public void muteEvents(boolean mute);
}
