package org.alfresco.jlan.server;


public class FileFilterMode
{
    public static enum Mode
    {
        BASIC, ENHANCED;
    };

    private static ThreadLocal<Mode> mode = new ThreadLocal<Mode>()
    {
        protected Mode initialValue() {
            return Mode.BASIC;
        }
    };
    
	public static Mode setMode(Mode newMode)
	{
		Mode ret = mode.get();
		mode.set(newMode);
		return ret;
	}
	
	public static Mode getMode()
	{
		return mode.get();
	}
}
