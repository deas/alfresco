package org.alfresco.po.share.systemsummary;

import org.openqa.selenium.By;

/**
 * @author sergey.kardash on 4/12/14.
 */
public enum AdminConsoleLink
{

    SystemSummary(By.cssSelector("a[href$='admin-systemsummary']")),
    ActivitiesFeed(By.cssSelector("a[href$='admin-activitiesfeed']")),
    RepositoryServerClustering(By.cssSelector("a[href$='admin-clustering']"));

    public final By contentLocator;

    AdminConsoleLink(By adminConsoleLink)
    {
        this.contentLocator = adminConsoleLink;

    }

}
