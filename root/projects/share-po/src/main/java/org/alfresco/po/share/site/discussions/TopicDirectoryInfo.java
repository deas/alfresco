package org.alfresco.po.share.site.discussions;

import org.openqa.selenium.WebElement;

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
}
