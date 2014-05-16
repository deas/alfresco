package org.alfresco.po.share.adminconsole;

/**
 * @author Roman.Chul
 */
public enum Channel {
        Facebook("Facebook"),
        Flickr("Flickr"),
        LinkedIn("LinkedIn"),
        SlideShare("SlideShare"),
        Twitter("Twitter"),
        YouTube("YouTube");

        private String channelName;

        private Channel(String channel)
        {
            channelName = channel;
        }

        public String getChannelName() {
            return channelName;
        }
}
