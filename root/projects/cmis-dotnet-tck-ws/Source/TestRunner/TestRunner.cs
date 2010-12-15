/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
using System;
using System.Net;
using System.Net.Security;
using System.ServiceModel;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Configuration;
using System.Reflection;
using WcfCmisWSTests.CmisServices;

///
/// author: Stas Sokolovsky
///
namespace WcfCmisWSTests
{
    public class CmisTestRunner
    {
        private const string TEST_METHOD_PREFIX = "test";

        private Statistics generalStatistics = new Statistics();

        private CmisLogger logger = CmisLogger.getInstance();

        public Statistics GeneralStatistics
        {
            get { return generalStatistics; }
        }

        public void run(object targetObject, string targetObjectName)
        {
            logger.log("Testing " + targetObjectName);
            logger.log("----------------------------");
            Statistics statistics = new Statistics();
            Type targetType = targetObject.GetType();
            MethodInfo[] methods = targetType.GetMethods();
            MethodInfo initializer = getMethodOrNull(targetType, "initialize", typeof(string));
            MethodInfo releaser = getMethodOrNull(targetType, "release", typeof(string));
            foreach (MethodInfo method in methods)
            {
                if (method.Name.StartsWith(TEST_METHOD_PREFIX))
                {
                    statistics.TotalCount++;
                    string fullname = "'" + targetObjectName + "->" + method.Name + "'";
                    try
                    {
                        logger.log("Executing test " + fullname);
                        if (null != initializer)
                        {
                            try
                            {
                                initializer.Invoke(targetObject, new object[] { method.Name });
                            }
                            catch (Exception e)
                            {
                                logger.log("WARNING: Initialization before " + fullname + " test executing was failed. Test can't be run. Error message: " + e.Message);
                                statistics.SkippedCount++;
                                logger.log(string.Empty);
                                continue;
                            }
                        }
                        long startTime = DateTime.Now.Ticks;
                        try
                        {
                            targetType.InvokeMember(method.Name, BindingFlags.InvokeMethod, null, targetObject, null);
                        }
                        catch (TargetInvocationException e)
                        {
                            throw e.InnerException;
                        }
                        long spentTime = (DateTime.Now.Ticks - startTime) / 10000;
                        statistics.AddTime(spentTime);
                        statistics.PassedCount++;
                        if (null != releaser)
                        {
                            try
                            {
                                releaser.Invoke(targetObject, new object[] { method.Name });
                            }
                            catch (Exception e1)
                            {
                                logger.log("--- WARNING: Releasing of test data was failed. Error message: " + e1.Message);
                            }
                        }
                        logger.log("Test was passed " + fullname + ". Time: " + spentTime + " ms");
                    }
                    catch (SkippedException se)
                    {
                        if (null != releaser)
                        {
                            try
                            {
                                releaser.Invoke(targetObject, new object[] { method.Name });
                            }
                            catch (Exception e1)
                            {
                                logger.log("--- WARNING: Releasing of test data was failed. Error message: " + e1.Message);
                            }
                        }
                        logger.log("Test was skipped " + fullname + ". Reason: " + se.Message);
                        statistics.SkippedCount++;
                    }
                    catch (Exception e)
                    {
                        if (null != releaser)
                        {
                            try
                            {
                                releaser.Invoke(targetObject, new object[] { method.Name });
                            }
                            catch (Exception e1)
                            {
                                logger.log("--- WARNING: Releasing of test data was failed. Error message: " + e1.Message);
                            }
                        }
                        logger.log("Test was failed " + fullname + ". Message: " + e.Message);
                        statistics.FailedCount++;
                    }
                    logger.log(string.Empty);
                }
            }
            logger.log("----------------------------");
            logger.log("Passed:   " + statistics.PassedCount);
            logger.log("Failed:   " + statistics.FailedCount);
            logger.log("Skipped:  " + statistics.SkippedCount);
            logger.log("Executed: " + statistics.TotalCount);
            logger.log("----------------------------");
            logger.log("Spent time: " + statistics.TotalSpentTime + " ms");
            logger.log();
            logger.log();

            this.generalStatistics.Add(statistics);
        }

        private MethodInfo getMethodOrNull(Type sourceType, string methodName, Type parameter)
        {
            try
            {
                if (null != parameter)
                {
                    return sourceType.GetMethod(methodName, new Type[] { parameter });
                }
                else
                {
                    return sourceType.GetMethod(methodName);
                }
            }
            catch (Exception)
            {
                return null;
            }
        }

        public Statistics getStatistics()
        {
            return generalStatistics;
        }

        public class Statistics
        {
            private int passedCount = 0;

            public int PassedCount
            {
                get { return passedCount; }
                set { passedCount = value; }
            }

            private int failedCount = 0;

            public int FailedCount
            {
                get { return failedCount; }
                set { failedCount = value; }
            }

            private int totalCount = 0;

            public int TotalCount
            {
                get { return totalCount; }
                set { totalCount = value; }
            }

            private int skippedCount = 0;

            public int SkippedCount
            {
                get { return skippedCount; }
                set { skippedCount = value; }
            }

            private long totalSpentTime = 0;

            public long TotalSpentTime
            {
                get { return totalSpentTime; }
            }

            public void AddTime(long spentTime)
            {
                totalSpentTime += spentTime;
            }

            public void Add(Statistics statistics)
            {
                failedCount += statistics.failedCount;
                totalCount += statistics.totalCount;
                passedCount += statistics.passedCount;
                skippedCount += statistics.skippedCount;
                totalSpentTime += statistics.TotalSpentTime;
            }
        }
    }

}