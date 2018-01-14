package scrt.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public interface SRCTListener extends EventListener
{
	public void actionPerformed(SRCTEvent e);
	public void muteEvents(boolean mute);
}
