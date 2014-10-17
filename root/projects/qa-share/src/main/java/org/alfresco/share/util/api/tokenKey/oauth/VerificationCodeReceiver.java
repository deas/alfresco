package org.alfresco.share.util.api.tokenKey.oauth;

public interface VerificationCodeReceiver
{

    /** Returns the redirect URI. */
    String getRedirectUri() throws Exception;

    /** Releases any resources and stops any processes started. */
    void stop() throws Exception;

    /** Starting jetty */
    void startJetty();

}
