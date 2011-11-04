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
package org.alfresco.repo.search.impl.parsers;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.antlr.gunit.GrammarInfo;
import org.antlr.gunit.gUnitLexer;
import org.antlr.gunit.gUnitParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

public class FTSTest extends TestCase
{
    public FTSTest()
    {
        // TODO Auto-generated constructor stub
    }

    public FTSTest(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

    }

    public void testLexer() throws IOException, RecognitionException
    {
        ClassLoader cl = FTSTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/repo/search/impl/parsers/fts_test.gunit");

        CharStream input = new ANTLRInputStream(modelStream);

        gUnitExecutor executer = new gUnitExecutor(parse(input), "FTS");

        System.out.print(executer.execTest()); // unit test result

        assertEquals("Failures ", 0, executer.failures.size());
        assertEquals("Invalids ", 0, executer.invalids.size());
    }

    public void testLexerOutput() throws IOException, RecognitionException
    {
        CharStream cs = new ANTLRStringStream(".txt");
        FTSLexer lexer = new FTSLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
//        for(int i = 0; i < 10; i++)
//        {
//           System.out.println(tokens.LA(1));
//           System.out.println(tokens.LT(1));
//           tokens.consume();
//        }
        FTSParser parser = new FTSParser(tokens);
        parser.setMode(FTSParser.Mode.DEFAULT_CONJUNCTION);
        parser.setDefaultFieldConjunction(true);
        CommonTree ftsNode = (CommonTree) parser.ftsQuery().getTree();
        System.out.println(ftsNode);
        System.out.println(tokens.index());
        System.out.println(tokens.size());
    }

    private GrammarInfo parse(CharStream input) throws RecognitionException
    {
        gUnitLexer lexer = new gUnitLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        GrammarInfo grammarInfo = new GrammarInfo();
        gUnitParser parser = new gUnitParser(tokens, grammarInfo);
        parser.gUnitDef(); // parse gunit script and save elements to grammarInfo
        return grammarInfo;
    }

}
