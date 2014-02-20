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
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.Pair;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * @author andyh
 */
public class DateTimeTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;

    Iterator<Token> tokenIterator = null;

    public DateTimeTokenFilter(Reader in)
    {
        super(in);
        baseTokeniser = new WhitespaceTokenizer(in);
    }

    public Token next() throws IOException
    {
        if (tokenIterator == null)
        {
            buildIterator();
        }
        if (tokenIterator.hasNext())
        {
            return tokenIterator.next();
        }
        else
        {
            return null;
        }
    }

    public void buildIterator() throws IOException
    {
        Token candidate;
        ArrayList<Token> tokens = new ArrayList<Token>();
        while ((candidate = baseTokeniser.next()) != null)
        {
            Date date;
            int resolution; 
            if (candidate.termText().equalsIgnoreCase("now"))
            {
                date = new Date();
                resolution = Calendar.MILLISECOND;
            }
            else if (candidate.termText().equalsIgnoreCase("today"))
            {
                date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                resolution = Calendar.DAY_OF_MONTH;
            }
            else
            {
                try
                {
                    Pair<Date, Integer> parsed = CachingDateFormat.lenientParse(candidate.termText(), Calendar.YEAR);
                    date = parsed.getFirst();
                    resolution = parsed.getSecond();
                }
                catch (ParseException e)
                {
                    continue;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            Token token;

            if(resolution >= Calendar.YEAR)
            {
                // four digits
                token = new Token("YE" + cal.get(Calendar.YEAR), candidate.startOffset(), candidate.startOffset(), candidate.type());
                tokens.add(token);
            }

            if(resolution >= Calendar.MONTH)
            {
                // 2 digits
                int month = cal.get(Calendar.MONTH);
                if (month < 10)
                {
                    token = new Token("MO0" + month, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("MO" + month, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            if(resolution >= Calendar.DAY_OF_MONTH)
            {
                int day = cal.get(Calendar.DAY_OF_MONTH);
                if (day < 10)
                {
                    token = new Token("DA0" + day, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("DA" + day, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            if(resolution >= Calendar.HOUR_OF_DAY)
            {
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if (hour < 10)
                {
                    token = new Token("HO0" + hour, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("HO" + hour, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            if(resolution >= Calendar.MINUTE)
            {
                int minute = cal.get(Calendar.MINUTE);
                if (minute < 10)
                {
                    token = new Token("MI0" + minute, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("MI" + minute, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            if(resolution >= Calendar.SECOND)
            {
                int second = cal.get(Calendar.SECOND);
                if (second < 10)
                {
                    token = new Token("SE0" + second, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("SE" + second, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            if(resolution >= Calendar.MILLISECOND)
            {
                int millis = cal.get(Calendar.MILLISECOND);
                if (millis < 10)
                {
                    token = new Token("MS00" + millis, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else if (millis < 100)
                {
                    token = new Token("MS0" + millis, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
                else
                {
                    token = new Token("MS" + millis, candidate.startOffset(), candidate.startOffset(), candidate.type());
                    tokens.add(token);
                }
            }

            break;
        }

        tokenIterator = tokens.iterator();
    }
}