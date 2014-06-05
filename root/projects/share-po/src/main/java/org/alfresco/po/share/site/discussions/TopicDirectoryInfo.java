package org.alfresco.po.share.site.discussions;

/**
 *
 * @author Marina.Nenadovets
 */
public interface TopicDirectoryInfo
{
    /**
     * Click on View Topic
     *
     * @return
     */
    TopicViewPage viewTopic ();

    /**
     * Click on Edit Topic
     * @return
     */
    NewTopicForm editTopic ();

    /**
     * Click on Delete Topic
     * @return
     */
    DiscussionsPage deleteTopic();

    /**
     * Verify whether edit topic is displayed
     * @return
     */
    boolean isEditTopicDisplayed ();

    /**
     * Verify whether delete topic is displayed
     * @return
     */
    boolean isDeleteTopicDisplayed ();
}
