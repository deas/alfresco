// $ANTLR 3.5 W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g 2014-07-01 21:52:09

    package org.alfresco.repo.search.impl.parsers;
    import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class CMISParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL_COLUMNS", "AND", "ANY", "AS", 
		"ASC", "BOOLEAN_LITERAL", "BY", "COLON", "COLUMN", "COLUMNS", "COLUMN_REF", 
		"COMMA", "CONJUNCTION", "CONTAINS", "DATETIME_LITERAL", "DECIMAL_INTEGER_LITERAL", 
		"DECIMAL_NUMERAL", "DESC", "DIGIT", "DISJUNCTION", "DOT", "DOTDOT", "DOTSTAR", 
		"DOUBLE_QUOTE", "E", "EQUALS", "EXPONENT", "FALSE", "FLOATING_POINT_LITERAL", 
		"FROM", "FUNCTION", "GREATERTHAN", "GREATERTHANOREQUALS", "ID", "IN", 
		"INNER", "IN_FOLDER", "IN_TREE", "IS", "JOIN", "LEFT", "LESSTHAN", "LESSTHANOREQUALS", 
		"LIKE", "LIST", "LPAREN", "MINUS", "NEGATION", "NON_ZERO_DIGIT", "NOT", 
		"NOTEQUALS", "NULL", "NUMERIC_LITERAL", "ON", "OR", "ORDER", "OUTER", 
		"PARAMETER", "PLUS", "PRED_CHILD", "PRED_COMPARISON", "PRED_DESCENDANT", 
		"PRED_EXISTS", "PRED_FTS", "PRED_IN", "PRED_LIKE", "QUALIFIER", "QUERY", 
		"QUOTED_STRING", "RPAREN", "SCORE", "SELECT", "SIGNED_INTEGER", "SINGLE_VALUED_PROPERTY", 
		"SORT_SPECIFICATION", "SOURCE", "STAR", "STRING_LITERAL", "TABLE", "TABLE_REF", 
		"TILDA", "TIMESTAMP", "TRUE", "WHERE", "WS", "ZERO_DIGIT"
	};
	public static final int EOF=-1;
	public static final int ALL_COLUMNS=4;
	public static final int AND=5;
	public static final int ANY=6;
	public static final int AS=7;
	public static final int ASC=8;
	public static final int BOOLEAN_LITERAL=9;
	public static final int BY=10;
	public static final int COLON=11;
	public static final int COLUMN=12;
	public static final int COLUMNS=13;
	public static final int COLUMN_REF=14;
	public static final int COMMA=15;
	public static final int CONJUNCTION=16;
	public static final int CONTAINS=17;
	public static final int DATETIME_LITERAL=18;
	public static final int DECIMAL_INTEGER_LITERAL=19;
	public static final int DECIMAL_NUMERAL=20;
	public static final int DESC=21;
	public static final int DIGIT=22;
	public static final int DISJUNCTION=23;
	public static final int DOT=24;
	public static final int DOTDOT=25;
	public static final int DOTSTAR=26;
	public static final int DOUBLE_QUOTE=27;
	public static final int E=28;
	public static final int EQUALS=29;
	public static final int EXPONENT=30;
	public static final int FALSE=31;
	public static final int FLOATING_POINT_LITERAL=32;
	public static final int FROM=33;
	public static final int FUNCTION=34;
	public static final int GREATERTHAN=35;
	public static final int GREATERTHANOREQUALS=36;
	public static final int ID=37;
	public static final int IN=38;
	public static final int INNER=39;
	public static final int IN_FOLDER=40;
	public static final int IN_TREE=41;
	public static final int IS=42;
	public static final int JOIN=43;
	public static final int LEFT=44;
	public static final int LESSTHAN=45;
	public static final int LESSTHANOREQUALS=46;
	public static final int LIKE=47;
	public static final int LIST=48;
	public static final int LPAREN=49;
	public static final int MINUS=50;
	public static final int NEGATION=51;
	public static final int NON_ZERO_DIGIT=52;
	public static final int NOT=53;
	public static final int NOTEQUALS=54;
	public static final int NULL=55;
	public static final int NUMERIC_LITERAL=56;
	public static final int ON=57;
	public static final int OR=58;
	public static final int ORDER=59;
	public static final int OUTER=60;
	public static final int PARAMETER=61;
	public static final int PLUS=62;
	public static final int PRED_CHILD=63;
	public static final int PRED_COMPARISON=64;
	public static final int PRED_DESCENDANT=65;
	public static final int PRED_EXISTS=66;
	public static final int PRED_FTS=67;
	public static final int PRED_IN=68;
	public static final int PRED_LIKE=69;
	public static final int QUALIFIER=70;
	public static final int QUERY=71;
	public static final int QUOTED_STRING=72;
	public static final int RPAREN=73;
	public static final int SCORE=74;
	public static final int SELECT=75;
	public static final int SIGNED_INTEGER=76;
	public static final int SINGLE_VALUED_PROPERTY=77;
	public static final int SORT_SPECIFICATION=78;
	public static final int SOURCE=79;
	public static final int STAR=80;
	public static final int STRING_LITERAL=81;
	public static final int TABLE=82;
	public static final int TABLE_REF=83;
	public static final int TILDA=84;
	public static final int TIMESTAMP=85;
	public static final int TRUE=86;
	public static final int WHERE=87;
	public static final int WS=88;
	public static final int ZERO_DIGIT=89;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public CMISParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public CMISParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return CMISParser.tokenNames; }
	@Override public String getGrammarFileName() { return "W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g"; }


	    private Stack<String> paraphrases = new Stack<String>();
	    
	    private boolean strict = false;

	    /**
	     * CMIS strict
	     */
	    public boolean strict()
	    {
	        return strict;
	    }
		
	    public void setStrict(boolean strict)
	    {
	        this.strict = strict;
	    }
	        
	    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
	    {
	        throw new MismatchedTokenException(ttype, input);
	    }
		
	    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException
	    {
	        throw e;
	    }
		
	    public String getErrorMessage(RecognitionException e, String[] tokenNames) 
	    {
	        List stack = getRuleInvocationStack(e, this.getClass().getName());
	        String msg = e.getMessage();
	        if ( e instanceof UnwantedTokenException ) 
		    {
	            UnwantedTokenException ute = (UnwantedTokenException)e;
	            String tokenName="<unknown>";
	            if ( ute.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[ute.expecting];
	            }
	            msg = "extraneous input " + getTokenErrorDisplay(ute.getUnexpectedToken())
	                + " expecting "+tokenName;
	        }
	        else if ( e instanceof MissingTokenException ) 
	        {
	            MissingTokenException mte = (MissingTokenException)e;
	            String tokenName="<unknown>";
	            if ( mte.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[mte.expecting];
	            }
	            msg = "missing " + tokenName+" at " + getTokenErrorDisplay(e.token)
	                + "  (" + getLongTokenErrorDisplay(e.token) +")";
	        }
	        else if ( e instanceof MismatchedTokenException ) 
	        {
	            MismatchedTokenException mte = (MismatchedTokenException)e;
	            String tokenName="<unknown>";
	            if ( mte.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else
	            {
	                tokenName = tokenNames[mte.expecting];
	            }
	            msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting " + tokenName +"  (" + getLongTokenErrorDisplay(e.token) + ")";
	        }
	        else if ( e instanceof MismatchedTreeNodeException ) 
	        {
	            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
	            String tokenName="<unknown>";
	            if ( mtne.expecting==Token.EOF )  
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[mtne.expecting];
	            }
	            msg = "mismatched tree node: " + mtne.node + " expecting " + tokenName;
	        }
	        else if ( e instanceof NoViableAltException ) 
	        {
	            NoViableAltException nvae = (NoViableAltException)e;
	            msg = "no viable alternative at input " + getTokenErrorDisplay(e.token)
	                + "\n\t (decision=" + nvae.decisionNumber
	                + " state " + nvae.stateNumber + ")" 
	                + " decision=<<" + nvae.grammarDecisionDescription + ">>";
	        }
	        else if ( e instanceof EarlyExitException ) 
	        {
	            //EarlyExitException eee = (EarlyExitException)e;
	            // for development, can add "(decision="+eee.decisionNumber+")"
	            msg = "required (...)+ loop did not match anything at input " + getTokenErrorDisplay(e.token);
	        }
		    else if ( e instanceof MismatchedSetException ) 
		    {
		        MismatchedSetException mse = (MismatchedSetException)e;
		        msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting set " + mse.expecting;
	        }
	        else if ( e instanceof MismatchedNotSetException ) 
	        {
	            MismatchedNotSetException mse = (MismatchedNotSetException)e;
	            msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting set " + mse.expecting;
	        }
	        else if ( e instanceof FailedPredicateException ) 
	        {
	            FailedPredicateException fpe = (FailedPredicateException)e;
	            msg = "rule " + fpe.ruleName + " failed predicate: {" + fpe.predicateText + "}?";
	        }
			
	        if(paraphrases.size() > 0)
	        {
	            String paraphrase = (String)paraphrases.peek();
	            msg = msg+" "+paraphrase;
	        }
	        return msg +"\n\t"+stack;
	    }
		
	    public String getLongTokenErrorDisplay(Token t)
	    {
	        return t.toString();
	    }
	    

	    public String getErrorString(RecognitionException e)
	    {
	        String hdr = getErrorHeader(e);
	        String msg = getErrorMessage(e, this.getTokenNames());
	        return hdr+" "+msg;
	    }


	public static class query_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "query"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:286:1: query : SELECT selectList fromClause ( whereClause )? ( orderByClause )? EOF -> ^( QUERY selectList fromClause ( whereClause )? ( orderByClause )? ) ;
	public final CMISParser.query_return query() throws RecognitionException {
		CMISParser.query_return retval = new CMISParser.query_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token SELECT1=null;
		Token EOF6=null;
		ParserRuleReturnScope selectList2 =null;
		ParserRuleReturnScope fromClause3 =null;
		ParserRuleReturnScope whereClause4 =null;
		ParserRuleReturnScope orderByClause5 =null;

		Object SELECT1_tree=null;
		Object EOF6_tree=null;
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleTokenStream stream_SELECT=new RewriteRuleTokenStream(adaptor,"token SELECT");
		RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
		RewriteRuleSubtreeStream stream_orderByClause=new RewriteRuleSubtreeStream(adaptor,"rule orderByClause");
		RewriteRuleSubtreeStream stream_selectList=new RewriteRuleSubtreeStream(adaptor,"rule selectList");
		RewriteRuleSubtreeStream stream_fromClause=new RewriteRuleSubtreeStream(adaptor,"rule fromClause");


		    paraphrases.push("in query"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:295:9: ( SELECT selectList fromClause ( whereClause )? ( orderByClause )? EOF -> ^( QUERY selectList fromClause ( whereClause )? ( orderByClause )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:296:9: SELECT selectList fromClause ( whereClause )? ( orderByClause )? EOF
			{
			SELECT1=(Token)match(input,SELECT,FOLLOW_SELECT_in_query416); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_SELECT.add(SELECT1);

			pushFollow(FOLLOW_selectList_in_query418);
			selectList2=selectList();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_selectList.add(selectList2.getTree());
			pushFollow(FOLLOW_fromClause_in_query420);
			fromClause3=fromClause();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fromClause.add(fromClause3.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:296:38: ( whereClause )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==WHERE) ) {
				alt1=1;
			}
			switch (alt1) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:296:38: whereClause
					{
					pushFollow(FOLLOW_whereClause_in_query422);
					whereClause4=whereClause();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_whereClause.add(whereClause4.getTree());
					}
					break;

			}

			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:296:51: ( orderByClause )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==ORDER) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:296:51: orderByClause
					{
					pushFollow(FOLLOW_orderByClause_in_query425);
					orderByClause5=orderByClause();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_orderByClause.add(orderByClause5.getTree());
					}
					break;

			}

			EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_query428); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EOF.add(EOF6);

			// AST REWRITE
			// elements: fromClause, orderByClause, whereClause, selectList
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 297:17: -> ^( QUERY selectList fromClause ( whereClause )? ( orderByClause )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:298:25: ^( QUERY selectList fromClause ( whereClause )? ( orderByClause )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(QUERY, "QUERY"), root_1);
				adaptor.addChild(root_1, stream_selectList.nextTree());
				adaptor.addChild(root_1, stream_fromClause.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:298:55: ( whereClause )?
				if ( stream_whereClause.hasNext() ) {
					adaptor.addChild(root_1, stream_whereClause.nextTree());
				}
				stream_whereClause.reset();

				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:298:68: ( orderByClause )?
				if ( stream_orderByClause.hasNext() ) {
					adaptor.addChild(root_1, stream_orderByClause.nextTree());
				}
				stream_orderByClause.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "query"


	public static class selectList_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "selectList"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:301:1: selectList : ( STAR -> ^( ALL_COLUMNS ) | selectSubList ( COMMA selectSubList )* -> ^( COLUMNS ( selectSubList )+ ) );
	public final CMISParser.selectList_return selectList() throws RecognitionException {
		CMISParser.selectList_return retval = new CMISParser.selectList_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token STAR7=null;
		Token COMMA9=null;
		ParserRuleReturnScope selectSubList8 =null;
		ParserRuleReturnScope selectSubList10 =null;

		Object STAR7_tree=null;
		Object COMMA9_tree=null;
		RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_selectSubList=new RewriteRuleSubtreeStream(adaptor,"rule selectSubList");


		    paraphrases.push("in select list"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:310:9: ( STAR -> ^( ALL_COLUMNS ) | selectSubList ( COMMA selectSubList )* -> ^( COLUMNS ( selectSubList )+ ) )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==STAR) ) {
				alt4=1;
			}
			else if ( (LA4_0==ID) ) {
				alt4=2;
			}
			else if ( (LA4_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==SCORE) ) {
				alt4=2;
			}
			else if ( (LA4_0==SELECT) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==AS) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==FROM) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==JOIN) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==INNER) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==LEFT) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==OUTER) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==ON) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==WHERE) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==OR) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==AND) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==NOT) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==IN) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==LIKE) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==IS) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==NULL) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==ANY) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==CONTAINS) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==IN_FOLDER) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==IN_TREE) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==ORDER) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==BY) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==ASC) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==DESC) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==TIMESTAMP) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==TRUE) && ((strict == false))) {
				alt4=2;
			}
			else if ( (LA4_0==FALSE) && ((strict == false))) {
				alt4=2;
			}

			switch (alt4) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:311:9: STAR
					{
					STAR7=(Token)match(input,STAR,FOLLOW_STAR_in_selectList527); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_STAR.add(STAR7);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 312:17: -> ^( ALL_COLUMNS )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:313:25: ^( ALL_COLUMNS )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALL_COLUMNS, "ALL_COLUMNS"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:314:11: selectSubList ( COMMA selectSubList )*
					{
					pushFollow(FOLLOW_selectSubList_in_selectList585);
					selectSubList8=selectSubList();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_selectSubList.add(selectSubList8.getTree());
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:314:25: ( COMMA selectSubList )*
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( (LA3_0==COMMA) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:314:26: COMMA selectSubList
							{
							COMMA9=(Token)match(input,COMMA,FOLLOW_COMMA_in_selectList588); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA9);

							pushFollow(FOLLOW_selectSubList_in_selectList590);
							selectSubList10=selectSubList();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_selectSubList.add(selectSubList10.getTree());
							}
							break;

						default :
							break loop3;
						}
					}

					// AST REWRITE
					// elements: selectSubList
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 315:17: -> ^( COLUMNS ( selectSubList )+ )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:316:25: ^( COLUMNS ( selectSubList )+ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMNS, "COLUMNS"), root_1);
						if ( !(stream_selectSubList.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_selectSubList.hasNext() ) {
							adaptor.addChild(root_1, stream_selectSubList.nextTree());
						}
						stream_selectSubList.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "selectList"


	public static class selectSubList_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "selectSubList"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:319:1: selectSubList : ( valueExpression ( ( AS )? columnName )? -> ^( COLUMN valueExpression ( columnName )? ) | qualifier DOTSTAR -> ^( ALL_COLUMNS qualifier ) );
	public final CMISParser.selectSubList_return selectSubList() throws RecognitionException {
		CMISParser.selectSubList_return retval = new CMISParser.selectSubList_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AS12=null;
		Token DOTSTAR15=null;
		ParserRuleReturnScope valueExpression11 =null;
		ParserRuleReturnScope columnName13 =null;
		ParserRuleReturnScope qualifier14 =null;

		Object AS12_tree=null;
		Object DOTSTAR15_tree=null;
		RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
		RewriteRuleTokenStream stream_DOTSTAR=new RewriteRuleTokenStream(adaptor,"token DOTSTAR");
		RewriteRuleSubtreeStream stream_valueExpression=new RewriteRuleSubtreeStream(adaptor,"rule valueExpression");
		RewriteRuleSubtreeStream stream_columnName=new RewriteRuleSubtreeStream(adaptor,"rule columnName");
		RewriteRuleSubtreeStream stream_qualifier=new RewriteRuleSubtreeStream(adaptor,"rule qualifier");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:320:9: ( valueExpression ( ( AS )? columnName )? -> ^( COLUMN valueExpression ( columnName )? ) | qualifier DOTSTAR -> ^( ALL_COLUMNS qualifier ) )
			int alt7=2;
			alt7 = dfa7.predict(input);
			switch (alt7) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:321:9: valueExpression ( ( AS )? columnName )?
					{
					pushFollow(FOLLOW_valueExpression_in_selectSubList674);
					valueExpression11=valueExpression();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_valueExpression.add(valueExpression11.getTree());
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:321:25: ( ( AS )? columnName )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==AS) ) {
						alt6=1;
					}
					else if ( (LA6_0==ID) ) {
						alt6=1;
					}
					else if ( (LA6_0==DOUBLE_QUOTE) && ((strict == false))) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:321:26: ( AS )? columnName
							{
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:321:26: ( AS )?
							int alt5=2;
							int LA5_0 = input.LA(1);
							if ( (LA5_0==AS) ) {
								alt5=1;
							}
							switch (alt5) {
								case 1 :
									// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:321:26: AS
									{
									AS12=(Token)match(input,AS,FOLLOW_AS_in_selectSubList677); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_AS.add(AS12);

									}
									break;

							}

							pushFollow(FOLLOW_columnName_in_selectSubList680);
							columnName13=columnName();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_columnName.add(columnName13.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: valueExpression, columnName
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 322:17: -> ^( COLUMN valueExpression ( columnName )? )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:323:25: ^( COLUMN valueExpression ( columnName )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMN, "COLUMN"), root_1);
						adaptor.addChild(root_1, stream_valueExpression.nextTree());
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:323:50: ( columnName )?
						if ( stream_columnName.hasNext() ) {
							adaptor.addChild(root_1, stream_columnName.nextTree());
						}
						stream_columnName.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:324:11: qualifier DOTSTAR
					{
					pushFollow(FOLLOW_qualifier_in_selectSubList745);
					qualifier14=qualifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qualifier.add(qualifier14.getTree());
					DOTSTAR15=(Token)match(input,DOTSTAR,FOLLOW_DOTSTAR_in_selectSubList747); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOTSTAR.add(DOTSTAR15);

					// AST REWRITE
					// elements: qualifier
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 325:17: -> ^( ALL_COLUMNS qualifier )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:326:25: ^( ALL_COLUMNS qualifier )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALL_COLUMNS, "ALL_COLUMNS"), root_1);
						adaptor.addChild(root_1, stream_qualifier.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "selectSubList"


	public static class valueExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "valueExpression"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:329:1: valueExpression : ( columnReference -> columnReference | valueFunction -> valueFunction );
	public final CMISParser.valueExpression_return valueExpression() throws RecognitionException {
		CMISParser.valueExpression_return retval = new CMISParser.valueExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope columnReference16 =null;
		ParserRuleReturnScope valueFunction17 =null;

		RewriteRuleSubtreeStream stream_valueFunction=new RewriteRuleSubtreeStream(adaptor,"rule valueFunction");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");


		    paraphrases.push("in value expression"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:338:9: ( columnReference -> columnReference | valueFunction -> valueFunction )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==ID) ) {
				int LA8_1 = input.LA(2);
				if ( (LA8_1==DOT) ) {
					alt8=1;
				}
				else if ( (LA8_1==AS) ) {
					alt8=1;
				}
				else if ( (LA8_1==ID) ) {
					alt8=1;
				}
				else if ( (LA8_1==DOUBLE_QUOTE) ) {
					alt8=1;
				}
				else if ( (LA8_1==COMMA) ) {
					alt8=1;
				}
				else if ( (LA8_1==FROM) ) {
					alt8=1;
				}
				else if ( (LA8_1==EQUALS||(LA8_1 >= GREATERTHAN && LA8_1 <= GREATERTHANOREQUALS)||(LA8_1 >= LESSTHAN && LA8_1 <= LESSTHANOREQUALS)||LA8_1==NOTEQUALS) ) {
					alt8=1;
				}
				else if ( (LA8_1==LPAREN) && ((strict == false))) {
					alt8=2;
				}

			}
			else if ( (LA8_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt8=1;
			}
			else if ( (LA8_0==SCORE) ) {
				alt8=2;
			}
			else if ( (LA8_0==SELECT) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==AS) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==FROM) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==JOIN) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==INNER) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==LEFT) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==OUTER) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==ON) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==WHERE) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==OR) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==AND) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==NOT) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==IN) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==LIKE) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==IS) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==NULL) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==ANY) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==CONTAINS) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==IN_FOLDER) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==IN_TREE) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==ORDER) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==BY) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==ASC) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==DESC) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==TIMESTAMP) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==TRUE) && ((strict == false))) {
				alt8=2;
			}
			else if ( (LA8_0==FALSE) && ((strict == false))) {
				alt8=2;
			}

			switch (alt8) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:339:9: columnReference
					{
					pushFollow(FOLLOW_columnReference_in_valueExpression838);
					columnReference16=columnReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnReference.add(columnReference16.getTree());
					// AST REWRITE
					// elements: columnReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 340:17: -> columnReference
					{
						adaptor.addChild(root_0, stream_columnReference.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:341:11: valueFunction
					{
					pushFollow(FOLLOW_valueFunction_in_valueExpression870);
					valueFunction17=valueFunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_valueFunction.add(valueFunction17.getTree());
					// AST REWRITE
					// elements: valueFunction
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 342:17: -> valueFunction
					{
						adaptor.addChild(root_0, stream_valueFunction.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "valueExpression"


	public static class columnReference_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "columnReference"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:345:1: columnReference : ( qualifier DOT )? columnName -> ^( COLUMN_REF columnName ( qualifier )? ) ;
	public final CMISParser.columnReference_return columnReference() throws RecognitionException {
		CMISParser.columnReference_return retval = new CMISParser.columnReference_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DOT19=null;
		ParserRuleReturnScope qualifier18 =null;
		ParserRuleReturnScope columnName20 =null;

		Object DOT19_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleSubtreeStream stream_columnName=new RewriteRuleSubtreeStream(adaptor,"rule columnName");
		RewriteRuleSubtreeStream stream_qualifier=new RewriteRuleSubtreeStream(adaptor,"rule qualifier");


		    paraphrases.push("in column reference"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:354:9: ( ( qualifier DOT )? columnName -> ^( COLUMN_REF columnName ( qualifier )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:355:9: ( qualifier DOT )? columnName
			{
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:355:9: ( qualifier DOT )?
			int alt9=2;
			alt9 = dfa9.predict(input);
			switch (alt9) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:355:10: qualifier DOT
					{
					pushFollow(FOLLOW_qualifier_in_columnReference934);
					qualifier18=qualifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qualifier.add(qualifier18.getTree());
					DOT19=(Token)match(input,DOT,FOLLOW_DOT_in_columnReference936); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT19);

					}
					break;

			}

			pushFollow(FOLLOW_columnName_in_columnReference940);
			columnName20=columnName();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnName.add(columnName20.getTree());
			// AST REWRITE
			// elements: qualifier, columnName
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 356:17: -> ^( COLUMN_REF columnName ( qualifier )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:357:25: ^( COLUMN_REF columnName ( qualifier )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMN_REF, "COLUMN_REF"), root_1);
				adaptor.addChild(root_1, stream_columnName.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:357:49: ( qualifier )?
				if ( stream_qualifier.hasNext() ) {
					adaptor.addChild(root_1, stream_qualifier.nextTree());
				}
				stream_qualifier.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "columnReference"


	public static class valueFunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "valueFunction"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:360:1: valueFunction : (cmisFunctionName= cmisFunction LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $cmisFunctionName LPAREN ( functionArgument )* RPAREN ) |{...}? =>functionName= keyWordOrId LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $functionName LPAREN ( functionArgument )* RPAREN ) );
	public final CMISParser.valueFunction_return valueFunction() throws RecognitionException {
		CMISParser.valueFunction_return retval = new CMISParser.valueFunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN21=null;
		Token RPAREN23=null;
		Token LPAREN24=null;
		Token RPAREN26=null;
		ParserRuleReturnScope cmisFunctionName =null;
		ParserRuleReturnScope functionName =null;
		ParserRuleReturnScope functionArgument22 =null;
		ParserRuleReturnScope functionArgument25 =null;

		Object LPAREN21_tree=null;
		Object RPAREN23_tree=null;
		Object LPAREN24_tree=null;
		Object RPAREN26_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_cmisFunction=new RewriteRuleSubtreeStream(adaptor,"rule cmisFunction");
		RewriteRuleSubtreeStream stream_keyWordOrId=new RewriteRuleSubtreeStream(adaptor,"rule keyWordOrId");
		RewriteRuleSubtreeStream stream_functionArgument=new RewriteRuleSubtreeStream(adaptor,"rule functionArgument");


		    paraphrases.push("in function"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:369:9: (cmisFunctionName= cmisFunction LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $cmisFunctionName LPAREN ( functionArgument )* RPAREN ) |{...}? =>functionName= keyWordOrId LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $functionName LPAREN ( functionArgument )* RPAREN ) )
			int alt12=2;
			alt12 = dfa12.predict(input);
			switch (alt12) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:370:9: cmisFunctionName= cmisFunction LPAREN ( functionArgument )* RPAREN
					{
					pushFollow(FOLLOW_cmisFunction_in_valueFunction1036);
					cmisFunctionName=cmisFunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisFunction.add(cmisFunctionName.getTree());
					LPAREN21=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_valueFunction1038); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN21);

					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:370:46: ( functionArgument )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==ID) ) {
							alt10=1;
						}
						else if ( (LA10_0==DOUBLE_QUOTE) && ((strict == false))) {
							alt10=1;
						}
						else if ( (LA10_0==FLOATING_POINT_LITERAL) ) {
							alt10=1;
						}
						else if ( (LA10_0==DECIMAL_INTEGER_LITERAL) ) {
							alt10=1;
						}
						else if ( (LA10_0==QUOTED_STRING) ) {
							alt10=1;
						}
						else if ( (LA10_0==TRUE) ) {
							alt10=1;
						}
						else if ( (LA10_0==FALSE) ) {
							alt10=1;
						}
						else if ( (LA10_0==TIMESTAMP) ) {
							alt10=1;
						}
						else if ( (LA10_0==COLON) && ((strict == false))) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:370:46: functionArgument
							{
							pushFollow(FOLLOW_functionArgument_in_valueFunction1040);
							functionArgument22=functionArgument();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument22.getTree());
							}
							break;

						default :
							break loop10;
						}
					}

					RPAREN23=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_valueFunction1043); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN23);

					// AST REWRITE
					// elements: functionArgument, cmisFunctionName, LPAREN, RPAREN
					// token labels: 
					// rule labels: retval, cmisFunctionName
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
					RewriteRuleSubtreeStream stream_cmisFunctionName=new RewriteRuleSubtreeStream(adaptor,"rule cmisFunctionName",cmisFunctionName!=null?cmisFunctionName.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 371:17: -> ^( FUNCTION $cmisFunctionName LPAREN ( functionArgument )* RPAREN )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:372:25: ^( FUNCTION $cmisFunctionName LPAREN ( functionArgument )* RPAREN )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION, "FUNCTION"), root_1);
						adaptor.addChild(root_1, stream_cmisFunctionName.nextTree());
						adaptor.addChild(root_1, stream_LPAREN.nextNode());
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:372:61: ( functionArgument )*
						while ( stream_functionArgument.hasNext() ) {
							adaptor.addChild(root_1, stream_functionArgument.nextTree());
						}
						stream_functionArgument.reset();

						adaptor.addChild(root_1, stream_RPAREN.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:373:11: {...}? =>functionName= keyWordOrId LPAREN ( functionArgument )* RPAREN
					{
					if ( !((strict == false)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "valueFunction", "strict == false");
					}
					pushFollow(FOLLOW_keyWordOrId_in_valueFunction1116);
					functionName=keyWordOrId();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_keyWordOrId.add(functionName.getTree());
					LPAREN24=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_valueFunction1118); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN24);

					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:373:64: ( functionArgument )*
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( (LA11_0==ID) ) {
							alt11=1;
						}
						else if ( (LA11_0==DOUBLE_QUOTE) && ((strict == false))) {
							alt11=1;
						}
						else if ( (LA11_0==FLOATING_POINT_LITERAL) ) {
							alt11=1;
						}
						else if ( (LA11_0==DECIMAL_INTEGER_LITERAL) ) {
							alt11=1;
						}
						else if ( (LA11_0==QUOTED_STRING) ) {
							alt11=1;
						}
						else if ( (LA11_0==TRUE) ) {
							alt11=1;
						}
						else if ( (LA11_0==FALSE) ) {
							alt11=1;
						}
						else if ( (LA11_0==TIMESTAMP) ) {
							alt11=1;
						}
						else if ( (LA11_0==COLON) && ((strict == false))) {
							alt11=1;
						}

						switch (alt11) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:373:64: functionArgument
							{
							pushFollow(FOLLOW_functionArgument_in_valueFunction1120);
							functionArgument25=functionArgument();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_functionArgument.add(functionArgument25.getTree());
							}
							break;

						default :
							break loop11;
						}
					}

					RPAREN26=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_valueFunction1123); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN26);

					// AST REWRITE
					// elements: functionName, functionArgument, LPAREN, RPAREN
					// token labels: 
					// rule labels: retval, functionName
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
					RewriteRuleSubtreeStream stream_functionName=new RewriteRuleSubtreeStream(adaptor,"rule functionName",functionName!=null?functionName.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 374:17: -> ^( FUNCTION $functionName LPAREN ( functionArgument )* RPAREN )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:375:25: ^( FUNCTION $functionName LPAREN ( functionArgument )* RPAREN )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION, "FUNCTION"), root_1);
						adaptor.addChild(root_1, stream_functionName.nextTree());
						adaptor.addChild(root_1, stream_LPAREN.nextNode());
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:375:57: ( functionArgument )*
						while ( stream_functionArgument.hasNext() ) {
							adaptor.addChild(root_1, stream_functionArgument.nextTree());
						}
						stream_functionArgument.reset();

						adaptor.addChild(root_1, stream_RPAREN.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "valueFunction"


	public static class functionArgument_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "functionArgument"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:378:1: functionArgument : ( qualifier DOT columnName -> ^( COLUMN_REF columnName qualifier ) | identifier | literalOrParameterName );
	public final CMISParser.functionArgument_return functionArgument() throws RecognitionException {
		CMISParser.functionArgument_return retval = new CMISParser.functionArgument_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DOT28=null;
		ParserRuleReturnScope qualifier27 =null;
		ParserRuleReturnScope columnName29 =null;
		ParserRuleReturnScope identifier30 =null;
		ParserRuleReturnScope literalOrParameterName31 =null;

		Object DOT28_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleSubtreeStream stream_columnName=new RewriteRuleSubtreeStream(adaptor,"rule columnName");
		RewriteRuleSubtreeStream stream_qualifier=new RewriteRuleSubtreeStream(adaptor,"rule qualifier");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:379:9: ( qualifier DOT columnName -> ^( COLUMN_REF columnName qualifier ) | identifier | literalOrParameterName )
			int alt13=3;
			alt13 = dfa13.predict(input);
			switch (alt13) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:380:9: qualifier DOT columnName
					{
					pushFollow(FOLLOW_qualifier_in_functionArgument1212);
					qualifier27=qualifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qualifier.add(qualifier27.getTree());
					DOT28=(Token)match(input,DOT,FOLLOW_DOT_in_functionArgument1214); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT28);

					pushFollow(FOLLOW_columnName_in_functionArgument1216);
					columnName29=columnName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnName.add(columnName29.getTree());
					// AST REWRITE
					// elements: qualifier, columnName
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 381:17: -> ^( COLUMN_REF columnName qualifier )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:382:25: ^( COLUMN_REF columnName qualifier )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(COLUMN_REF, "COLUMN_REF"), root_1);
						adaptor.addChild(root_1, stream_columnName.nextTree());
						adaptor.addChild(root_1, stream_qualifier.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:383:11: identifier
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_identifier_in_functionArgument1278);
					identifier30=identifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, identifier30.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:384:11: literalOrParameterName
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_literalOrParameterName_in_functionArgument1290);
					literalOrParameterName31=literalOrParameterName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literalOrParameterName31.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "functionArgument"


	public static class qualifier_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "qualifier"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:387:1: qualifier : ( ( tableName )=> tableName -> tableName | correlationName -> correlationName );
	public final CMISParser.qualifier_return qualifier() throws RecognitionException {
		CMISParser.qualifier_return retval = new CMISParser.qualifier_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope tableName32 =null;
		ParserRuleReturnScope correlationName33 =null;

		RewriteRuleSubtreeStream stream_correlationName=new RewriteRuleSubtreeStream(adaptor,"rule correlationName");
		RewriteRuleSubtreeStream stream_tableName=new RewriteRuleSubtreeStream(adaptor,"rule tableName");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:388:9: ( ( tableName )=> tableName -> tableName | correlationName -> correlationName )
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==ID) ) {
				int LA14_1 = input.LA(2);
				if ( (synpred1_CMIS()) ) {
					alt14=1;
				}
				else if ( (true) ) {
					alt14=2;
				}

			}
			else if ( (LA14_0==DOUBLE_QUOTE) && ((strict == false))) {
				int LA14_2 = input.LA(2);
				if ( (LA14_2==SELECT) && ((strict == false))) {
					int LA14_5 = input.LA(3);
					if ( (LA14_5==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==AS) && ((strict == false))) {
					int LA14_6 = input.LA(3);
					if ( (LA14_6==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==FROM) && ((strict == false))) {
					int LA14_7 = input.LA(3);
					if ( (LA14_7==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==JOIN) && ((strict == false))) {
					int LA14_8 = input.LA(3);
					if ( (LA14_8==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==INNER) && ((strict == false))) {
					int LA14_9 = input.LA(3);
					if ( (LA14_9==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==LEFT) && ((strict == false))) {
					int LA14_10 = input.LA(3);
					if ( (LA14_10==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==OUTER) && ((strict == false))) {
					int LA14_11 = input.LA(3);
					if ( (LA14_11==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==ON) && ((strict == false))) {
					int LA14_12 = input.LA(3);
					if ( (LA14_12==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==WHERE) && ((strict == false))) {
					int LA14_13 = input.LA(3);
					if ( (LA14_13==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==OR) && ((strict == false))) {
					int LA14_14 = input.LA(3);
					if ( (LA14_14==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==AND) && ((strict == false))) {
					int LA14_15 = input.LA(3);
					if ( (LA14_15==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==NOT) && ((strict == false))) {
					int LA14_16 = input.LA(3);
					if ( (LA14_16==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==IN) && ((strict == false))) {
					int LA14_17 = input.LA(3);
					if ( (LA14_17==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==LIKE) && ((strict == false))) {
					int LA14_18 = input.LA(3);
					if ( (LA14_18==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==IS) && ((strict == false))) {
					int LA14_19 = input.LA(3);
					if ( (LA14_19==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==NULL) && ((strict == false))) {
					int LA14_20 = input.LA(3);
					if ( (LA14_20==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==ANY) && ((strict == false))) {
					int LA14_21 = input.LA(3);
					if ( (LA14_21==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==CONTAINS) && ((strict == false))) {
					int LA14_22 = input.LA(3);
					if ( (LA14_22==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==IN_FOLDER) && ((strict == false))) {
					int LA14_23 = input.LA(3);
					if ( (LA14_23==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==IN_TREE) && ((strict == false))) {
					int LA14_24 = input.LA(3);
					if ( (LA14_24==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==ORDER) && ((strict == false))) {
					int LA14_25 = input.LA(3);
					if ( (LA14_25==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==BY) && ((strict == false))) {
					int LA14_26 = input.LA(3);
					if ( (LA14_26==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==ASC) && ((strict == false))) {
					int LA14_27 = input.LA(3);
					if ( (LA14_27==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==DESC) && ((strict == false))) {
					int LA14_28 = input.LA(3);
					if ( (LA14_28==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==TIMESTAMP) && ((strict == false))) {
					int LA14_29 = input.LA(3);
					if ( (LA14_29==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==TRUE) && ((strict == false))) {
					int LA14_30 = input.LA(3);
					if ( (LA14_30==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==FALSE) && ((strict == false))) {
					int LA14_31 = input.LA(3);
					if ( (LA14_31==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==SCORE) && ((strict == false))) {
					int LA14_32 = input.LA(3);
					if ( (LA14_32==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}
				else if ( (LA14_2==ID) && ((strict == false))) {
					int LA14_33 = input.LA(3);
					if ( (LA14_33==DOUBLE_QUOTE) && ((strict == false))) {
						int LA14_34 = input.LA(4);
						if ( (((strict == false)&&synpred1_CMIS())) ) {
							alt14=1;
						}
						else if ( ((strict == false)) ) {
							alt14=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 14, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

				}

			}

			switch (alt14) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:389:9: ( tableName )=> tableName
					{
					pushFollow(FOLLOW_tableName_in_qualifier1329);
					tableName32=tableName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_tableName.add(tableName32.getTree());
					// AST REWRITE
					// elements: tableName
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 390:17: -> tableName
					{
						adaptor.addChild(root_0, stream_tableName.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:391:11: correlationName
					{
					pushFollow(FOLLOW_correlationName_in_qualifier1361);
					correlationName33=correlationName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_correlationName.add(correlationName33.getTree());
					// AST REWRITE
					// elements: correlationName
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 392:17: -> correlationName
					{
						adaptor.addChild(root_0, stream_correlationName.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "qualifier"


	public static class fromClause_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fromClause"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:395:1: fromClause : FROM tableReference -> tableReference ;
	public final CMISParser.fromClause_return fromClause() throws RecognitionException {
		CMISParser.fromClause_return retval = new CMISParser.fromClause_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FROM34=null;
		ParserRuleReturnScope tableReference35 =null;

		Object FROM34_tree=null;
		RewriteRuleTokenStream stream_FROM=new RewriteRuleTokenStream(adaptor,"token FROM");
		RewriteRuleSubtreeStream stream_tableReference=new RewriteRuleSubtreeStream(adaptor,"rule tableReference");


		    paraphrases.push("in fromClause"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:404:9: ( FROM tableReference -> tableReference )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:405:9: FROM tableReference
			{
			FROM34=(Token)match(input,FROM,FOLLOW_FROM_in_fromClause1424); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_FROM.add(FROM34);

			pushFollow(FOLLOW_tableReference_in_fromClause1426);
			tableReference35=tableReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_tableReference.add(tableReference35.getTree());
			// AST REWRITE
			// elements: tableReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 406:17: -> tableReference
			{
				adaptor.addChild(root_0, stream_tableReference.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fromClause"


	public static class tableReference_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "tableReference"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:409:1: tableReference : singleTable ( joinedTable )* -> ^( SOURCE singleTable ( joinedTable )* ) ;
	public final CMISParser.tableReference_return tableReference() throws RecognitionException {
		CMISParser.tableReference_return retval = new CMISParser.tableReference_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope singleTable36 =null;
		ParserRuleReturnScope joinedTable37 =null;

		RewriteRuleSubtreeStream stream_singleTable=new RewriteRuleSubtreeStream(adaptor,"rule singleTable");
		RewriteRuleSubtreeStream stream_joinedTable=new RewriteRuleSubtreeStream(adaptor,"rule joinedTable");


		    paraphrases.push("in tableReference"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:418:9: ( singleTable ( joinedTable )* -> ^( SOURCE singleTable ( joinedTable )* ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:419:9: singleTable ( joinedTable )*
			{
			pushFollow(FOLLOW_singleTable_in_tableReference1489);
			singleTable36=singleTable();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_singleTable.add(singleTable36.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:419:21: ( joinedTable )*
			loop15:
			while (true) {
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( (LA15_0==INNER||(LA15_0 >= JOIN && LA15_0 <= LEFT)) ) {
					alt15=1;
				}

				switch (alt15) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:419:21: joinedTable
					{
					pushFollow(FOLLOW_joinedTable_in_tableReference1491);
					joinedTable37=joinedTable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_joinedTable.add(joinedTable37.getTree());
					}
					break;

				default :
					break loop15;
				}
			}

			// AST REWRITE
			// elements: joinedTable, singleTable
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 420:17: -> ^( SOURCE singleTable ( joinedTable )* )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:421:25: ^( SOURCE singleTable ( joinedTable )* )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SOURCE, "SOURCE"), root_1);
				adaptor.addChild(root_1, stream_singleTable.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:421:46: ( joinedTable )*
				while ( stream_joinedTable.hasNext() ) {
					adaptor.addChild(root_1, stream_joinedTable.nextTree());
				}
				stream_joinedTable.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "tableReference"


	public static class singleTable_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "singleTable"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:428:1: singleTable : ( simpleTable -> simpleTable | complexTable -> ^( TABLE complexTable ) );
	public final CMISParser.singleTable_return singleTable() throws RecognitionException {
		CMISParser.singleTable_return retval = new CMISParser.singleTable_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope simpleTable38 =null;
		ParserRuleReturnScope complexTable39 =null;

		RewriteRuleSubtreeStream stream_simpleTable=new RewriteRuleSubtreeStream(adaptor,"rule simpleTable");
		RewriteRuleSubtreeStream stream_complexTable=new RewriteRuleSubtreeStream(adaptor,"rule complexTable");


		    paraphrases.push("in singleTable"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:437:9: ( simpleTable -> simpleTable | complexTable -> ^( TABLE complexTable ) )
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==ID) ) {
				alt16=1;
			}
			else if ( (LA16_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt16=1;
			}
			else if ( (LA16_0==LPAREN) ) {
				alt16=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:438:9: simpleTable
					{
					pushFollow(FOLLOW_simpleTable_in_singleTable1589);
					simpleTable38=simpleTable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_simpleTable.add(simpleTable38.getTree());
					// AST REWRITE
					// elements: simpleTable
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 439:17: -> simpleTable
					{
						adaptor.addChild(root_0, stream_simpleTable.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:440:11: complexTable
					{
					pushFollow(FOLLOW_complexTable_in_singleTable1621);
					complexTable39=complexTable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_complexTable.add(complexTable39.getTree());
					// AST REWRITE
					// elements: complexTable
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 441:17: -> ^( TABLE complexTable )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:442:25: ^( TABLE complexTable )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TABLE, "TABLE"), root_1);
						adaptor.addChild(root_1, stream_complexTable.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "singleTable"


	public static class simpleTable_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "simpleTable"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:445:1: simpleTable : tableName ( ( AS )? correlationName )? -> ^( TABLE_REF tableName ( correlationName )? ) ;
	public final CMISParser.simpleTable_return simpleTable() throws RecognitionException {
		CMISParser.simpleTable_return retval = new CMISParser.simpleTable_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AS41=null;
		ParserRuleReturnScope tableName40 =null;
		ParserRuleReturnScope correlationName42 =null;

		Object AS41_tree=null;
		RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
		RewriteRuleSubtreeStream stream_correlationName=new RewriteRuleSubtreeStream(adaptor,"rule correlationName");
		RewriteRuleSubtreeStream stream_tableName=new RewriteRuleSubtreeStream(adaptor,"rule tableName");


		    paraphrases.push("in simpleTable"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:454:9: ( tableName ( ( AS )? correlationName )? -> ^( TABLE_REF tableName ( correlationName )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:455:9: tableName ( ( AS )? correlationName )?
			{
			pushFollow(FOLLOW_tableName_in_simpleTable1712);
			tableName40=tableName();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_tableName.add(tableName40.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:455:19: ( ( AS )? correlationName )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==AS) ) {
				alt18=1;
			}
			else if ( (LA18_0==ID) ) {
				alt18=1;
			}
			else if ( (LA18_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:455:20: ( AS )? correlationName
					{
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:455:20: ( AS )?
					int alt17=2;
					int LA17_0 = input.LA(1);
					if ( (LA17_0==AS) ) {
						alt17=1;
					}
					switch (alt17) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:455:20: AS
							{
							AS41=(Token)match(input,AS,FOLLOW_AS_in_simpleTable1715); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_AS.add(AS41);

							}
							break;

					}

					pushFollow(FOLLOW_correlationName_in_simpleTable1718);
					correlationName42=correlationName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_correlationName.add(correlationName42.getTree());
					}
					break;

			}

			// AST REWRITE
			// elements: correlationName, tableName
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 456:17: -> ^( TABLE_REF tableName ( correlationName )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:457:25: ^( TABLE_REF tableName ( correlationName )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TABLE_REF, "TABLE_REF"), root_1);
				adaptor.addChild(root_1, stream_tableName.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:457:47: ( correlationName )?
				if ( stream_correlationName.hasNext() ) {
					adaptor.addChild(root_1, stream_correlationName.nextTree());
				}
				stream_correlationName.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "simpleTable"


	public static class joinedTable_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "joinedTable"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:460:1: joinedTable : ( joinType )? JOIN tableReference joinSpecification -> ^( JOIN tableReference ( joinType )? joinSpecification ) ;
	public final CMISParser.joinedTable_return joinedTable() throws RecognitionException {
		CMISParser.joinedTable_return retval = new CMISParser.joinedTable_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token JOIN44=null;
		ParserRuleReturnScope joinType43 =null;
		ParserRuleReturnScope tableReference45 =null;
		ParserRuleReturnScope joinSpecification46 =null;

		Object JOIN44_tree=null;
		RewriteRuleTokenStream stream_JOIN=new RewriteRuleTokenStream(adaptor,"token JOIN");
		RewriteRuleSubtreeStream stream_tableReference=new RewriteRuleSubtreeStream(adaptor,"rule tableReference");
		RewriteRuleSubtreeStream stream_joinType=new RewriteRuleSubtreeStream(adaptor,"rule joinType");
		RewriteRuleSubtreeStream stream_joinSpecification=new RewriteRuleSubtreeStream(adaptor,"rule joinSpecification");


		    paraphrases.push("in joinedTable"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:469:9: ( ( joinType )? JOIN tableReference joinSpecification -> ^( JOIN tableReference ( joinType )? joinSpecification ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:470:9: ( joinType )? JOIN tableReference joinSpecification
			{
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:470:9: ( joinType )?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==INNER||LA19_0==LEFT) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:470:9: joinType
					{
					pushFollow(FOLLOW_joinType_in_joinedTable1814);
					joinType43=joinType();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_joinType.add(joinType43.getTree());
					}
					break;

			}

			JOIN44=(Token)match(input,JOIN,FOLLOW_JOIN_in_joinedTable1817); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_JOIN.add(JOIN44);

			pushFollow(FOLLOW_tableReference_in_joinedTable1819);
			tableReference45=tableReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_tableReference.add(tableReference45.getTree());
			pushFollow(FOLLOW_joinSpecification_in_joinedTable1821);
			joinSpecification46=joinSpecification();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_joinSpecification.add(joinSpecification46.getTree());
			// AST REWRITE
			// elements: joinSpecification, joinType, tableReference, JOIN
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 471:17: -> ^( JOIN tableReference ( joinType )? joinSpecification )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:472:25: ^( JOIN tableReference ( joinType )? joinSpecification )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot(stream_JOIN.nextNode(), root_1);
				adaptor.addChild(root_1, stream_tableReference.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:472:47: ( joinType )?
				if ( stream_joinType.hasNext() ) {
					adaptor.addChild(root_1, stream_joinType.nextTree());
				}
				stream_joinType.reset();

				adaptor.addChild(root_1, stream_joinSpecification.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "joinedTable"


	public static class complexTable_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "complexTable"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:475:1: complexTable : ( ( LPAREN singleTable ( joinedTable )+ RPAREN )=> LPAREN singleTable ( joinedTable )+ RPAREN -> ^( SOURCE singleTable ( joinedTable )+ ) | LPAREN complexTable RPAREN -> complexTable );
	public final CMISParser.complexTable_return complexTable() throws RecognitionException {
		CMISParser.complexTable_return retval = new CMISParser.complexTable_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN47=null;
		Token RPAREN50=null;
		Token LPAREN51=null;
		Token RPAREN53=null;
		ParserRuleReturnScope singleTable48 =null;
		ParserRuleReturnScope joinedTable49 =null;
		ParserRuleReturnScope complexTable52 =null;

		Object LPAREN47_tree=null;
		Object RPAREN50_tree=null;
		Object LPAREN51_tree=null;
		Object RPAREN53_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_singleTable=new RewriteRuleSubtreeStream(adaptor,"rule singleTable");
		RewriteRuleSubtreeStream stream_joinedTable=new RewriteRuleSubtreeStream(adaptor,"rule joinedTable");
		RewriteRuleSubtreeStream stream_complexTable=new RewriteRuleSubtreeStream(adaptor,"rule complexTable");


		    paraphrases.push("in complexTable"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:484:9: ( ( LPAREN singleTable ( joinedTable )+ RPAREN )=> LPAREN singleTable ( joinedTable )+ RPAREN -> ^( SOURCE singleTable ( joinedTable )+ ) | LPAREN complexTable RPAREN -> complexTable )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==LPAREN) ) {
				int LA21_1 = input.LA(2);
				if ( (synpred2_CMIS()) ) {
					alt21=1;
				}
				else if ( (true) ) {
					alt21=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:9: ( LPAREN singleTable ( joinedTable )+ RPAREN )=> LPAREN singleTable ( joinedTable )+ RPAREN
					{
					LPAREN47=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_complexTable1930); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN47);

					pushFollow(FOLLOW_singleTable_in_complexTable1932);
					singleTable48=singleTable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_singleTable.add(singleTable48.getTree());
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:72: ( joinedTable )+
					int cnt20=0;
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( (LA20_0==INNER||(LA20_0 >= JOIN && LA20_0 <= LEFT)) ) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:72: joinedTable
							{
							pushFollow(FOLLOW_joinedTable_in_complexTable1934);
							joinedTable49=joinedTable();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_joinedTable.add(joinedTable49.getTree());
							}
							break;

						default :
							if ( cnt20 >= 1 ) break loop20;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(20, input);
							throw eee;
						}
						cnt20++;
					}

					RPAREN50=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_complexTable1937); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN50);

					// AST REWRITE
					// elements: joinedTable, singleTable
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 486:17: -> ^( SOURCE singleTable ( joinedTable )+ )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:487:25: ^( SOURCE singleTable ( joinedTable )+ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SOURCE, "SOURCE"), root_1);
						adaptor.addChild(root_1, stream_singleTable.nextTree());
						if ( !(stream_joinedTable.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_joinedTable.hasNext() ) {
							adaptor.addChild(root_1, stream_joinedTable.nextTree());
						}
						stream_joinedTable.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:488:11: LPAREN complexTable RPAREN
					{
					LPAREN51=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_complexTable2000); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN51);

					pushFollow(FOLLOW_complexTable_in_complexTable2002);
					complexTable52=complexTable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_complexTable.add(complexTable52.getTree());
					RPAREN53=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_complexTable2004); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN53);

					// AST REWRITE
					// elements: complexTable
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 489:17: -> complexTable
					{
						adaptor.addChild(root_0, stream_complexTable.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "complexTable"


	public static class joinType_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "joinType"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:492:1: joinType : ( INNER -> INNER | LEFT ( OUTER )? -> LEFT );
	public final CMISParser.joinType_return joinType() throws RecognitionException {
		CMISParser.joinType_return retval = new CMISParser.joinType_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token INNER54=null;
		Token LEFT55=null;
		Token OUTER56=null;

		Object INNER54_tree=null;
		Object LEFT55_tree=null;
		Object OUTER56_tree=null;
		RewriteRuleTokenStream stream_OUTER=new RewriteRuleTokenStream(adaptor,"token OUTER");
		RewriteRuleTokenStream stream_INNER=new RewriteRuleTokenStream(adaptor,"token INNER");
		RewriteRuleTokenStream stream_LEFT=new RewriteRuleTokenStream(adaptor,"token LEFT");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:493:9: ( INNER -> INNER | LEFT ( OUTER )? -> LEFT )
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==INNER) ) {
				alt23=1;
			}
			else if ( (LA23_0==LEFT) ) {
				alt23=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:494:9: INNER
					{
					INNER54=(Token)match(input,INNER,FOLLOW_INNER_in_joinType2057); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_INNER.add(INNER54);

					// AST REWRITE
					// elements: INNER
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 495:17: -> INNER
					{
						adaptor.addChild(root_0, stream_INNER.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:496:11: LEFT ( OUTER )?
					{
					LEFT55=(Token)match(input,LEFT,FOLLOW_LEFT_in_joinType2089); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LEFT.add(LEFT55);

					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:496:16: ( OUTER )?
					int alt22=2;
					int LA22_0 = input.LA(1);
					if ( (LA22_0==OUTER) ) {
						alt22=1;
					}
					switch (alt22) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:496:16: OUTER
							{
							OUTER56=(Token)match(input,OUTER,FOLLOW_OUTER_in_joinType2091); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_OUTER.add(OUTER56);

							}
							break;

					}

					// AST REWRITE
					// elements: LEFT
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 497:17: -> LEFT
					{
						adaptor.addChild(root_0, stream_LEFT.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "joinType"


	public static class joinSpecification_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "joinSpecification"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:500:1: joinSpecification : ON lhs= columnReference EQUALS rhs= columnReference -> ^( ON $lhs EQUALS $rhs) ;
	public final CMISParser.joinSpecification_return joinSpecification() throws RecognitionException {
		CMISParser.joinSpecification_return retval = new CMISParser.joinSpecification_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ON57=null;
		Token EQUALS58=null;
		ParserRuleReturnScope lhs =null;
		ParserRuleReturnScope rhs =null;

		Object ON57_tree=null;
		Object EQUALS58_tree=null;
		RewriteRuleTokenStream stream_ON=new RewriteRuleTokenStream(adaptor,"token ON");
		RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");


		    paraphrases.push("in join condition"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:509:9: ( ON lhs= columnReference EQUALS rhs= columnReference -> ^( ON $lhs EQUALS $rhs) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:510:9: ON lhs= columnReference EQUALS rhs= columnReference
			{
			ON57=(Token)match(input,ON,FOLLOW_ON_in_joinSpecification2155); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ON.add(ON57);

			pushFollow(FOLLOW_columnReference_in_joinSpecification2159);
			lhs=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(lhs.getTree());
			EQUALS58=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_joinSpecification2161); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS58);

			pushFollow(FOLLOW_columnReference_in_joinSpecification2165);
			rhs=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(rhs.getTree());
			// AST REWRITE
			// elements: ON, EQUALS, rhs, lhs
			// token labels: 
			// rule labels: retval, rhs, lhs
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
			RewriteRuleSubtreeStream stream_rhs=new RewriteRuleSubtreeStream(adaptor,"rule rhs",rhs!=null?rhs.getTree():null);
			RewriteRuleSubtreeStream stream_lhs=new RewriteRuleSubtreeStream(adaptor,"rule lhs",lhs!=null?lhs.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 511:17: -> ^( ON $lhs EQUALS $rhs)
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:512:25: ^( ON $lhs EQUALS $rhs)
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot(stream_ON.nextNode(), root_1);
				adaptor.addChild(root_1, stream_lhs.nextTree());
				adaptor.addChild(root_1, stream_EQUALS.nextNode());
				adaptor.addChild(root_1, stream_rhs.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "joinSpecification"


	public static class whereClause_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "whereClause"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:519:1: whereClause : WHERE searchOrCondition -> searchOrCondition ;
	public final CMISParser.whereClause_return whereClause() throws RecognitionException {
		CMISParser.whereClause_return retval = new CMISParser.whereClause_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token WHERE59=null;
		ParserRuleReturnScope searchOrCondition60 =null;

		Object WHERE59_tree=null;
		RewriteRuleTokenStream stream_WHERE=new RewriteRuleTokenStream(adaptor,"token WHERE");
		RewriteRuleSubtreeStream stream_searchOrCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchOrCondition");


		    paraphrases.push("in where"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:528:9: ( WHERE searchOrCondition -> searchOrCondition )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:529:9: WHERE searchOrCondition
			{
			WHERE59=(Token)match(input,WHERE,FOLLOW_WHERE_in_whereClause2265); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_WHERE.add(WHERE59);

			pushFollow(FOLLOW_searchOrCondition_in_whereClause2267);
			searchOrCondition60=searchOrCondition();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_searchOrCondition.add(searchOrCondition60.getTree());
			// AST REWRITE
			// elements: searchOrCondition
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 530:17: -> searchOrCondition
			{
				adaptor.addChild(root_0, stream_searchOrCondition.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "whereClause"


	public static class searchOrCondition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "searchOrCondition"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:536:1: searchOrCondition : searchAndCondition ( OR searchAndCondition )* -> ^( DISJUNCTION ( searchAndCondition )+ ) ;
	public final CMISParser.searchOrCondition_return searchOrCondition() throws RecognitionException {
		CMISParser.searchOrCondition_return retval = new CMISParser.searchOrCondition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OR62=null;
		ParserRuleReturnScope searchAndCondition61 =null;
		ParserRuleReturnScope searchAndCondition63 =null;

		Object OR62_tree=null;
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
		RewriteRuleSubtreeStream stream_searchAndCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchAndCondition");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:537:9: ( searchAndCondition ( OR searchAndCondition )* -> ^( DISJUNCTION ( searchAndCondition )+ ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:538:9: searchAndCondition ( OR searchAndCondition )*
			{
			pushFollow(FOLLOW_searchAndCondition_in_searchOrCondition2322);
			searchAndCondition61=searchAndCondition();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_searchAndCondition.add(searchAndCondition61.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:538:28: ( OR searchAndCondition )*
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==OR) ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:538:29: OR searchAndCondition
					{
					OR62=(Token)match(input,OR,FOLLOW_OR_in_searchOrCondition2325); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OR.add(OR62);

					pushFollow(FOLLOW_searchAndCondition_in_searchOrCondition2327);
					searchAndCondition63=searchAndCondition();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_searchAndCondition.add(searchAndCondition63.getTree());
					}
					break;

				default :
					break loop24;
				}
			}

			// AST REWRITE
			// elements: searchAndCondition
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 539:17: -> ^( DISJUNCTION ( searchAndCondition )+ )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:540:25: ^( DISJUNCTION ( searchAndCondition )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);
				if ( !(stream_searchAndCondition.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_searchAndCondition.hasNext() ) {
					adaptor.addChild(root_1, stream_searchAndCondition.nextTree());
				}
				stream_searchAndCondition.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "searchOrCondition"


	public static class searchAndCondition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "searchAndCondition"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:546:1: searchAndCondition : searchNotCondition ( AND searchNotCondition )* -> ^( CONJUNCTION ( searchNotCondition )+ ) ;
	public final CMISParser.searchAndCondition_return searchAndCondition() throws RecognitionException {
		CMISParser.searchAndCondition_return retval = new CMISParser.searchAndCondition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AND65=null;
		ParserRuleReturnScope searchNotCondition64 =null;
		ParserRuleReturnScope searchNotCondition66 =null;

		Object AND65_tree=null;
		RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
		RewriteRuleSubtreeStream stream_searchNotCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchNotCondition");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:547:9: ( searchNotCondition ( AND searchNotCondition )* -> ^( CONJUNCTION ( searchNotCondition )+ ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:548:9: searchNotCondition ( AND searchNotCondition )*
			{
			pushFollow(FOLLOW_searchNotCondition_in_searchAndCondition2413);
			searchNotCondition64=searchNotCondition();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_searchNotCondition.add(searchNotCondition64.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:548:28: ( AND searchNotCondition )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==AND) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:548:29: AND searchNotCondition
					{
					AND65=(Token)match(input,AND,FOLLOW_AND_in_searchAndCondition2416); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AND.add(AND65);

					pushFollow(FOLLOW_searchNotCondition_in_searchAndCondition2418);
					searchNotCondition66=searchNotCondition();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_searchNotCondition.add(searchNotCondition66.getTree());
					}
					break;

				default :
					break loop25;
				}
			}

			// AST REWRITE
			// elements: searchNotCondition
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 549:17: -> ^( CONJUNCTION ( searchNotCondition )+ )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:550:25: ^( CONJUNCTION ( searchNotCondition )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);
				if ( !(stream_searchNotCondition.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_searchNotCondition.hasNext() ) {
					adaptor.addChild(root_1, stream_searchNotCondition.nextTree());
				}
				stream_searchNotCondition.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "searchAndCondition"


	public static class searchNotCondition_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "searchNotCondition"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:553:1: searchNotCondition : ( NOT searchTest -> ^( NEGATION searchTest ) | searchTest -> searchTest );
	public final CMISParser.searchNotCondition_return searchNotCondition() throws RecognitionException {
		CMISParser.searchNotCondition_return retval = new CMISParser.searchNotCondition_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NOT67=null;
		ParserRuleReturnScope searchTest68 =null;
		ParserRuleReturnScope searchTest69 =null;

		Object NOT67_tree=null;
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleSubtreeStream stream_searchTest=new RewriteRuleSubtreeStream(adaptor,"rule searchTest");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:554:9: ( NOT searchTest -> ^( NEGATION searchTest ) | searchTest -> searchTest )
			int alt26=2;
			alt26 = dfa26.predict(input);
			switch (alt26) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:555:9: NOT searchTest
					{
					NOT67=(Token)match(input,NOT,FOLLOW_NOT_in_searchNotCondition2502); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT67);

					pushFollow(FOLLOW_searchTest_in_searchNotCondition2504);
					searchTest68=searchTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_searchTest.add(searchTest68.getTree());
					// AST REWRITE
					// elements: searchTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 556:17: -> ^( NEGATION searchTest )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:557:25: ^( NEGATION searchTest )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NEGATION, "NEGATION"), root_1);
						adaptor.addChild(root_1, stream_searchTest.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:558:11: searchTest
					{
					pushFollow(FOLLOW_searchTest_in_searchNotCondition2564);
					searchTest69=searchTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_searchTest.add(searchTest69.getTree());
					// AST REWRITE
					// elements: searchTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 559:17: -> searchTest
					{
						adaptor.addChild(root_0, stream_searchTest.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "searchNotCondition"


	public static class searchTest_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "searchTest"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:562:1: searchTest : ( predicate -> predicate | LPAREN searchOrCondition RPAREN -> searchOrCondition );
	public final CMISParser.searchTest_return searchTest() throws RecognitionException {
		CMISParser.searchTest_return retval = new CMISParser.searchTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN71=null;
		Token RPAREN73=null;
		ParserRuleReturnScope predicate70 =null;
		ParserRuleReturnScope searchOrCondition72 =null;

		Object LPAREN71_tree=null;
		Object RPAREN73_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_predicate=new RewriteRuleSubtreeStream(adaptor,"rule predicate");
		RewriteRuleSubtreeStream stream_searchOrCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchOrCondition");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:563:9: ( predicate -> predicate | LPAREN searchOrCondition RPAREN -> searchOrCondition )
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==ID) ) {
				alt27=1;
			}
			else if ( (LA27_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==SCORE) ) {
				alt27=1;
			}
			else if ( (LA27_0==SELECT) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==AS) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==FROM) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==JOIN) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==INNER) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==LEFT) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==OUTER) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==ON) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==WHERE) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==OR) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==AND) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==NOT) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==IN) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==LIKE) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==IS) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==NULL) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==ANY) ) {
				alt27=1;
			}
			else if ( (LA27_0==CONTAINS) ) {
				alt27=1;
			}
			else if ( (LA27_0==IN_FOLDER) ) {
				alt27=1;
			}
			else if ( (LA27_0==IN_TREE) ) {
				alt27=1;
			}
			else if ( (LA27_0==ORDER) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==BY) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==ASC) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==DESC) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==TIMESTAMP) ) {
				alt27=1;
			}
			else if ( (LA27_0==TRUE) ) {
				alt27=1;
			}
			else if ( (LA27_0==FALSE) ) {
				alt27=1;
			}
			else if ( (LA27_0==FLOATING_POINT_LITERAL) ) {
				alt27=1;
			}
			else if ( (LA27_0==DECIMAL_INTEGER_LITERAL) ) {
				alt27=1;
			}
			else if ( (LA27_0==QUOTED_STRING) ) {
				alt27=1;
			}
			else if ( (LA27_0==COLON) && ((strict == false))) {
				alt27=1;
			}
			else if ( (LA27_0==LPAREN) ) {
				alt27=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:564:9: predicate
					{
					pushFollow(FOLLOW_predicate_in_searchTest2617);
					predicate70=predicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_predicate.add(predicate70.getTree());
					// AST REWRITE
					// elements: predicate
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 565:17: -> predicate
					{
						adaptor.addChild(root_0, stream_predicate.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:566:11: LPAREN searchOrCondition RPAREN
					{
					LPAREN71=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_searchTest2649); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN71);

					pushFollow(FOLLOW_searchOrCondition_in_searchTest2651);
					searchOrCondition72=searchOrCondition();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_searchOrCondition.add(searchOrCondition72.getTree());
					RPAREN73=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_searchTest2653); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN73);

					// AST REWRITE
					// elements: searchOrCondition
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 567:17: -> searchOrCondition
					{
						adaptor.addChild(root_0, stream_searchOrCondition.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "searchTest"


	public static class predicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "predicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:570:1: predicate : ( comparisonPredicate | inPredicate | likePredicate | nullPredicate | quantifiedComparisonPredicate | quantifiedInPredicate | textSearchPredicate | folderPredicate );
	public final CMISParser.predicate_return predicate() throws RecognitionException {
		CMISParser.predicate_return retval = new CMISParser.predicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope comparisonPredicate74 =null;
		ParserRuleReturnScope inPredicate75 =null;
		ParserRuleReturnScope likePredicate76 =null;
		ParserRuleReturnScope nullPredicate77 =null;
		ParserRuleReturnScope quantifiedComparisonPredicate78 =null;
		ParserRuleReturnScope quantifiedInPredicate79 =null;
		ParserRuleReturnScope textSearchPredicate80 =null;
		ParserRuleReturnScope folderPredicate81 =null;


		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:571:9: ( comparisonPredicate | inPredicate | likePredicate | nullPredicate | quantifiedComparisonPredicate | quantifiedInPredicate | textSearchPredicate | folderPredicate )
			int alt28=8;
			alt28 = dfa28.predict(input);
			switch (alt28) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:572:9: comparisonPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_comparisonPredicate_in_predicate2706);
					comparisonPredicate74=comparisonPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, comparisonPredicate74.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:573:11: inPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_inPredicate_in_predicate2718);
					inPredicate75=inPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, inPredicate75.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:574:11: likePredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_likePredicate_in_predicate2730);
					likePredicate76=likePredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, likePredicate76.getTree());

					}
					break;
				case 4 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:575:11: nullPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_nullPredicate_in_predicate2742);
					nullPredicate77=nullPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, nullPredicate77.getTree());

					}
					break;
				case 5 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:576:11: quantifiedComparisonPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_quantifiedComparisonPredicate_in_predicate2754);
					quantifiedComparisonPredicate78=quantifiedComparisonPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, quantifiedComparisonPredicate78.getTree());

					}
					break;
				case 6 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:577:11: quantifiedInPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_quantifiedInPredicate_in_predicate2766);
					quantifiedInPredicate79=quantifiedInPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, quantifiedInPredicate79.getTree());

					}
					break;
				case 7 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:578:11: textSearchPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_textSearchPredicate_in_predicate2778);
					textSearchPredicate80=textSearchPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, textSearchPredicate80.getTree());

					}
					break;
				case 8 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:579:11: folderPredicate
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_folderPredicate_in_predicate2790);
					folderPredicate81=folderPredicate();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, folderPredicate81.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "predicate"


	public static class comparisonPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "comparisonPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:582:1: comparisonPredicate : valueExpression compOp literalOrParameterName -> ^( PRED_COMPARISON SINGLE_VALUED_PROPERTY valueExpression compOp literalOrParameterName ) ;
	public final CMISParser.comparisonPredicate_return comparisonPredicate() throws RecognitionException {
		CMISParser.comparisonPredicate_return retval = new CMISParser.comparisonPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope valueExpression82 =null;
		ParserRuleReturnScope compOp83 =null;
		ParserRuleReturnScope literalOrParameterName84 =null;

		RewriteRuleSubtreeStream stream_valueExpression=new RewriteRuleSubtreeStream(adaptor,"rule valueExpression");
		RewriteRuleSubtreeStream stream_compOp=new RewriteRuleSubtreeStream(adaptor,"rule compOp");
		RewriteRuleSubtreeStream stream_literalOrParameterName=new RewriteRuleSubtreeStream(adaptor,"rule literalOrParameterName");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:583:9: ( valueExpression compOp literalOrParameterName -> ^( PRED_COMPARISON SINGLE_VALUED_PROPERTY valueExpression compOp literalOrParameterName ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:584:9: valueExpression compOp literalOrParameterName
			{
			pushFollow(FOLLOW_valueExpression_in_comparisonPredicate2823);
			valueExpression82=valueExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_valueExpression.add(valueExpression82.getTree());
			pushFollow(FOLLOW_compOp_in_comparisonPredicate2825);
			compOp83=compOp();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_compOp.add(compOp83.getTree());
			pushFollow(FOLLOW_literalOrParameterName_in_comparisonPredicate2827);
			literalOrParameterName84=literalOrParameterName();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_literalOrParameterName.add(literalOrParameterName84.getTree());
			// AST REWRITE
			// elements: valueExpression, compOp, literalOrParameterName
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 585:17: -> ^( PRED_COMPARISON SINGLE_VALUED_PROPERTY valueExpression compOp literalOrParameterName )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:586:25: ^( PRED_COMPARISON SINGLE_VALUED_PROPERTY valueExpression compOp literalOrParameterName )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_COMPARISON, "PRED_COMPARISON"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(SINGLE_VALUED_PROPERTY, "SINGLE_VALUED_PROPERTY"));
				adaptor.addChild(root_1, stream_valueExpression.nextTree());
				adaptor.addChild(root_1, stream_compOp.nextTree());
				adaptor.addChild(root_1, stream_literalOrParameterName.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "comparisonPredicate"


	public static class compOp_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "compOp"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:589:1: compOp : ( EQUALS | NOTEQUALS | LESSTHAN | GREATERTHAN | LESSTHANOREQUALS | GREATERTHANOREQUALS );
	public final CMISParser.compOp_return compOp() throws RecognitionException {
		CMISParser.compOp_return retval = new CMISParser.compOp_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set85=null;

		Object set85_tree=null;

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:590:9: ( EQUALS | NOTEQUALS | LESSTHAN | GREATERTHAN | LESSTHANOREQUALS | GREATERTHANOREQUALS )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:
			{
			root_0 = (Object)adaptor.nil();


			set85=input.LT(1);
			if ( input.LA(1)==EQUALS||(input.LA(1) >= GREATERTHAN && input.LA(1) <= GREATERTHANOREQUALS)||(input.LA(1) >= LESSTHAN && input.LA(1) <= LESSTHANOREQUALS)||input.LA(1)==NOTEQUALS ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set85));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "compOp"


	public static class literalOrParameterName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "literalOrParameterName"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:599:1: literalOrParameterName : ( literal |{...}? => parameterName );
	public final CMISParser.literalOrParameterName_return literalOrParameterName() throws RecognitionException {
		CMISParser.literalOrParameterName_return retval = new CMISParser.literalOrParameterName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope literal86 =null;
		ParserRuleReturnScope parameterName87 =null;


		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:600:9: ( literal |{...}? => parameterName )
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==FLOATING_POINT_LITERAL) ) {
				alt29=1;
			}
			else if ( (LA29_0==DECIMAL_INTEGER_LITERAL) ) {
				alt29=1;
			}
			else if ( (LA29_0==QUOTED_STRING) ) {
				alt29=1;
			}
			else if ( (LA29_0==TRUE) ) {
				alt29=1;
			}
			else if ( (LA29_0==FALSE) ) {
				alt29=1;
			}
			else if ( (LA29_0==TIMESTAMP) ) {
				alt29=1;
			}
			else if ( (LA29_0==COLON) && ((strict == false))) {
				alt29=2;
			}

			switch (alt29) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:601:9: literal
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_literal_in_literalOrParameterName3007);
					literal86=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, literal86.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:602:11: {...}? => parameterName
					{
					root_0 = (Object)adaptor.nil();


					if ( !((strict == false)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "literalOrParameterName", "strict == false");
					}
					pushFollow(FOLLOW_parameterName_in_literalOrParameterName3022);
					parameterName87=parameterName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, parameterName87.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literalOrParameterName"


	public static class literal_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "literal"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:605:1: literal : ( signedNumericLiteral | characterStringLiteral | booleanLiteral | datetimeLiteral );
	public final CMISParser.literal_return literal() throws RecognitionException {
		CMISParser.literal_return retval = new CMISParser.literal_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope signedNumericLiteral88 =null;
		ParserRuleReturnScope characterStringLiteral89 =null;
		ParserRuleReturnScope booleanLiteral90 =null;
		ParserRuleReturnScope datetimeLiteral91 =null;


		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:606:9: ( signedNumericLiteral | characterStringLiteral | booleanLiteral | datetimeLiteral )
			int alt30=4;
			switch ( input.LA(1) ) {
			case DECIMAL_INTEGER_LITERAL:
			case FLOATING_POINT_LITERAL:
				{
				alt30=1;
				}
				break;
			case QUOTED_STRING:
				{
				alt30=2;
				}
				break;
			case FALSE:
			case TRUE:
				{
				alt30=3;
				}
				break;
			case TIMESTAMP:
				{
				alt30=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 30, 0, input);
				throw nvae;
			}
			switch (alt30) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:607:9: signedNumericLiteral
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_signedNumericLiteral_in_literal3055);
					signedNumericLiteral88=signedNumericLiteral();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, signedNumericLiteral88.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:608:11: characterStringLiteral
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_characterStringLiteral_in_literal3067);
					characterStringLiteral89=characterStringLiteral();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, characterStringLiteral89.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:609:11: booleanLiteral
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_booleanLiteral_in_literal3079);
					booleanLiteral90=booleanLiteral();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral90.getTree());

					}
					break;
				case 4 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:610:11: datetimeLiteral
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_datetimeLiteral_in_literal3091);
					datetimeLiteral91=datetimeLiteral();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, datetimeLiteral91.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literal"


	public static class inPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "inPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:613:1: inPredicate : columnReference ( NOT )? IN LPAREN inValueList RPAREN -> ^( PRED_IN SINGLE_VALUED_PROPERTY columnReference inValueList ( NOT )? ) ;
	public final CMISParser.inPredicate_return inPredicate() throws RecognitionException {
		CMISParser.inPredicate_return retval = new CMISParser.inPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NOT93=null;
		Token IN94=null;
		Token LPAREN95=null;
		Token RPAREN97=null;
		ParserRuleReturnScope columnReference92 =null;
		ParserRuleReturnScope inValueList96 =null;

		Object NOT93_tree=null;
		Object IN94_tree=null;
		Object LPAREN95_tree=null;
		Object RPAREN97_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");
		RewriteRuleSubtreeStream stream_inValueList=new RewriteRuleSubtreeStream(adaptor,"rule inValueList");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:614:9: ( columnReference ( NOT )? IN LPAREN inValueList RPAREN -> ^( PRED_IN SINGLE_VALUED_PROPERTY columnReference inValueList ( NOT )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:615:9: columnReference ( NOT )? IN LPAREN inValueList RPAREN
			{
			pushFollow(FOLLOW_columnReference_in_inPredicate3124);
			columnReference92=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(columnReference92.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:615:25: ( NOT )?
			int alt31=2;
			int LA31_0 = input.LA(1);
			if ( (LA31_0==NOT) ) {
				alt31=1;
			}
			switch (alt31) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:615:25: NOT
					{
					NOT93=(Token)match(input,NOT,FOLLOW_NOT_in_inPredicate3126); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT93);

					}
					break;

			}

			IN94=(Token)match(input,IN,FOLLOW_IN_in_inPredicate3129); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_IN.add(IN94);

			LPAREN95=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_inPredicate3131); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN95);

			pushFollow(FOLLOW_inValueList_in_inPredicate3133);
			inValueList96=inValueList();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_inValueList.add(inValueList96.getTree());
			RPAREN97=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_inPredicate3135); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN97);

			// AST REWRITE
			// elements: NOT, inValueList, columnReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 616:17: -> ^( PRED_IN SINGLE_VALUED_PROPERTY columnReference inValueList ( NOT )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:617:25: ^( PRED_IN SINGLE_VALUED_PROPERTY columnReference inValueList ( NOT )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_IN, "PRED_IN"), root_1);
				adaptor.addChild(root_1, (Object)adaptor.create(SINGLE_VALUED_PROPERTY, "SINGLE_VALUED_PROPERTY"));
				adaptor.addChild(root_1, stream_columnReference.nextTree());
				adaptor.addChild(root_1, stream_inValueList.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:617:86: ( NOT )?
				if ( stream_NOT.hasNext() ) {
					adaptor.addChild(root_1, stream_NOT.nextNode());
				}
				stream_NOT.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "inPredicate"


	public static class inValueList_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "inValueList"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:620:1: inValueList : literalOrParameterName ( COMMA literalOrParameterName )* -> ^( LIST ( literalOrParameterName )+ ) ;
	public final CMISParser.inValueList_return inValueList() throws RecognitionException {
		CMISParser.inValueList_return retval = new CMISParser.inValueList_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COMMA99=null;
		ParserRuleReturnScope literalOrParameterName98 =null;
		ParserRuleReturnScope literalOrParameterName100 =null;

		Object COMMA99_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_literalOrParameterName=new RewriteRuleSubtreeStream(adaptor,"rule literalOrParameterName");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:621:9: ( literalOrParameterName ( COMMA literalOrParameterName )* -> ^( LIST ( literalOrParameterName )+ ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:622:9: literalOrParameterName ( COMMA literalOrParameterName )*
			{
			pushFollow(FOLLOW_literalOrParameterName_in_inValueList3223);
			literalOrParameterName98=literalOrParameterName();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_literalOrParameterName.add(literalOrParameterName98.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:622:32: ( COMMA literalOrParameterName )*
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0==COMMA) ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:622:33: COMMA literalOrParameterName
					{
					COMMA99=(Token)match(input,COMMA,FOLLOW_COMMA_in_inValueList3226); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA99);

					pushFollow(FOLLOW_literalOrParameterName_in_inValueList3228);
					literalOrParameterName100=literalOrParameterName();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_literalOrParameterName.add(literalOrParameterName100.getTree());
					}
					break;

				default :
					break loop32;
				}
			}

			// AST REWRITE
			// elements: literalOrParameterName
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 623:17: -> ^( LIST ( literalOrParameterName )+ )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:624:25: ^( LIST ( literalOrParameterName )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "LIST"), root_1);
				if ( !(stream_literalOrParameterName.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_literalOrParameterName.hasNext() ) {
					adaptor.addChild(root_1, stream_literalOrParameterName.nextTree());
				}
				stream_literalOrParameterName.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "inValueList"


	public static class likePredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "likePredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:627:1: likePredicate : columnReference ( NOT )? LIKE characterStringLiteral -> ^( PRED_LIKE columnReference characterStringLiteral ( NOT )? ) ;
	public final CMISParser.likePredicate_return likePredicate() throws RecognitionException {
		CMISParser.likePredicate_return retval = new CMISParser.likePredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NOT102=null;
		Token LIKE103=null;
		ParserRuleReturnScope columnReference101 =null;
		ParserRuleReturnScope characterStringLiteral104 =null;

		Object NOT102_tree=null;
		Object LIKE103_tree=null;
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleTokenStream stream_LIKE=new RewriteRuleTokenStream(adaptor,"token LIKE");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");
		RewriteRuleSubtreeStream stream_characterStringLiteral=new RewriteRuleSubtreeStream(adaptor,"rule characterStringLiteral");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:628:9: ( columnReference ( NOT )? LIKE characterStringLiteral -> ^( PRED_LIKE columnReference characterStringLiteral ( NOT )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:629:9: columnReference ( NOT )? LIKE characterStringLiteral
			{
			pushFollow(FOLLOW_columnReference_in_likePredicate3312);
			columnReference101=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(columnReference101.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:629:25: ( NOT )?
			int alt33=2;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==NOT) ) {
				alt33=1;
			}
			switch (alt33) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:629:25: NOT
					{
					NOT102=(Token)match(input,NOT,FOLLOW_NOT_in_likePredicate3314); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT102);

					}
					break;

			}

			LIKE103=(Token)match(input,LIKE,FOLLOW_LIKE_in_likePredicate3317); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LIKE.add(LIKE103);

			pushFollow(FOLLOW_characterStringLiteral_in_likePredicate3319);
			characterStringLiteral104=characterStringLiteral();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_characterStringLiteral.add(characterStringLiteral104.getTree());
			// AST REWRITE
			// elements: NOT, characterStringLiteral, columnReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 630:17: -> ^( PRED_LIKE columnReference characterStringLiteral ( NOT )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:631:25: ^( PRED_LIKE columnReference characterStringLiteral ( NOT )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_LIKE, "PRED_LIKE"), root_1);
				adaptor.addChild(root_1, stream_columnReference.nextTree());
				adaptor.addChild(root_1, stream_characterStringLiteral.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:631:76: ( NOT )?
				if ( stream_NOT.hasNext() ) {
					adaptor.addChild(root_1, stream_NOT.nextNode());
				}
				stream_NOT.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "likePredicate"


	public static class nullPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "nullPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:634:1: nullPredicate : ( columnReference IS NULL -> ^( PRED_EXISTS columnReference NOT ) | columnReference IS NOT NULL -> ^( PRED_EXISTS columnReference ) );
	public final CMISParser.nullPredicate_return nullPredicate() throws RecognitionException {
		CMISParser.nullPredicate_return retval = new CMISParser.nullPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token IS106=null;
		Token NULL107=null;
		Token IS109=null;
		Token NOT110=null;
		Token NULL111=null;
		ParserRuleReturnScope columnReference105 =null;
		ParserRuleReturnScope columnReference108 =null;

		Object IS106_tree=null;
		Object NULL107_tree=null;
		Object IS109_tree=null;
		Object NOT110_tree=null;
		Object NULL111_tree=null;
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleTokenStream stream_IS=new RewriteRuleTokenStream(adaptor,"token IS");
		RewriteRuleTokenStream stream_NULL=new RewriteRuleTokenStream(adaptor,"token NULL");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:635:9: ( columnReference IS NULL -> ^( PRED_EXISTS columnReference NOT ) | columnReference IS NOT NULL -> ^( PRED_EXISTS columnReference ) )
			int alt34=2;
			alt34 = dfa34.predict(input);
			switch (alt34) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:636:9: columnReference IS NULL
					{
					pushFollow(FOLLOW_columnReference_in_nullPredicate3405);
					columnReference105=columnReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnReference.add(columnReference105.getTree());
					IS106=(Token)match(input,IS,FOLLOW_IS_in_nullPredicate3407); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IS.add(IS106);

					NULL107=(Token)match(input,NULL,FOLLOW_NULL_in_nullPredicate3409); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NULL.add(NULL107);

					// AST REWRITE
					// elements: columnReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 637:17: -> ^( PRED_EXISTS columnReference NOT )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:638:25: ^( PRED_EXISTS columnReference NOT )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_EXISTS, "PRED_EXISTS"), root_1);
						adaptor.addChild(root_1, stream_columnReference.nextTree());
						adaptor.addChild(root_1, (Object)adaptor.create(NOT, "NOT"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:639:11: columnReference IS NOT NULL
					{
					pushFollow(FOLLOW_columnReference_in_nullPredicate3471);
					columnReference108=columnReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnReference.add(columnReference108.getTree());
					IS109=(Token)match(input,IS,FOLLOW_IS_in_nullPredicate3473); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IS.add(IS109);

					NOT110=(Token)match(input,NOT,FOLLOW_NOT_in_nullPredicate3475); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT110);

					NULL111=(Token)match(input,NULL,FOLLOW_NULL_in_nullPredicate3477); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NULL.add(NULL111);

					// AST REWRITE
					// elements: columnReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 640:17: -> ^( PRED_EXISTS columnReference )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:641:25: ^( PRED_EXISTS columnReference )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_EXISTS, "PRED_EXISTS"), root_1);
						adaptor.addChild(root_1, stream_columnReference.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "nullPredicate"


	public static class quantifiedComparisonPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "quantifiedComparisonPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:644:1: quantifiedComparisonPredicate : literalOrParameterName compOp ANY columnReference -> ^( PRED_COMPARISON ANY literalOrParameterName compOp columnReference ) ;
	public final CMISParser.quantifiedComparisonPredicate_return quantifiedComparisonPredicate() throws RecognitionException {
		CMISParser.quantifiedComparisonPredicate_return retval = new CMISParser.quantifiedComparisonPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ANY114=null;
		ParserRuleReturnScope literalOrParameterName112 =null;
		ParserRuleReturnScope compOp113 =null;
		ParserRuleReturnScope columnReference115 =null;

		Object ANY114_tree=null;
		RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");
		RewriteRuleSubtreeStream stream_compOp=new RewriteRuleSubtreeStream(adaptor,"rule compOp");
		RewriteRuleSubtreeStream stream_literalOrParameterName=new RewriteRuleSubtreeStream(adaptor,"rule literalOrParameterName");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:645:9: ( literalOrParameterName compOp ANY columnReference -> ^( PRED_COMPARISON ANY literalOrParameterName compOp columnReference ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:646:9: literalOrParameterName compOp ANY columnReference
			{
			pushFollow(FOLLOW_literalOrParameterName_in_quantifiedComparisonPredicate3558);
			literalOrParameterName112=literalOrParameterName();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_literalOrParameterName.add(literalOrParameterName112.getTree());
			pushFollow(FOLLOW_compOp_in_quantifiedComparisonPredicate3560);
			compOp113=compOp();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_compOp.add(compOp113.getTree());
			ANY114=(Token)match(input,ANY,FOLLOW_ANY_in_quantifiedComparisonPredicate3562); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ANY.add(ANY114);

			pushFollow(FOLLOW_columnReference_in_quantifiedComparisonPredicate3564);
			columnReference115=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(columnReference115.getTree());
			// AST REWRITE
			// elements: compOp, columnReference, ANY, literalOrParameterName
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 647:17: -> ^( PRED_COMPARISON ANY literalOrParameterName compOp columnReference )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:648:25: ^( PRED_COMPARISON ANY literalOrParameterName compOp columnReference )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_COMPARISON, "PRED_COMPARISON"), root_1);
				adaptor.addChild(root_1, stream_ANY.nextNode());
				adaptor.addChild(root_1, stream_literalOrParameterName.nextTree());
				adaptor.addChild(root_1, stream_compOp.nextTree());
				adaptor.addChild(root_1, stream_columnReference.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "quantifiedComparisonPredicate"


	public static class quantifiedInPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "quantifiedInPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:651:1: quantifiedInPredicate : ANY columnReference ( NOT )? IN LPAREN inValueList RPAREN -> ^( PRED_IN ANY columnReference inValueList ( NOT )? ) ;
	public final CMISParser.quantifiedInPredicate_return quantifiedInPredicate() throws RecognitionException {
		CMISParser.quantifiedInPredicate_return retval = new CMISParser.quantifiedInPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ANY116=null;
		Token NOT118=null;
		Token IN119=null;
		Token LPAREN120=null;
		Token RPAREN122=null;
		ParserRuleReturnScope columnReference117 =null;
		ParserRuleReturnScope inValueList121 =null;

		Object ANY116_tree=null;
		Object NOT118_tree=null;
		Object IN119_tree=null;
		Object LPAREN120_tree=null;
		Object RPAREN122_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
		RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");
		RewriteRuleSubtreeStream stream_inValueList=new RewriteRuleSubtreeStream(adaptor,"rule inValueList");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:652:9: ( ANY columnReference ( NOT )? IN LPAREN inValueList RPAREN -> ^( PRED_IN ANY columnReference inValueList ( NOT )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:653:9: ANY columnReference ( NOT )? IN LPAREN inValueList RPAREN
			{
			ANY116=(Token)match(input,ANY,FOLLOW_ANY_in_quantifiedInPredicate3651); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ANY.add(ANY116);

			pushFollow(FOLLOW_columnReference_in_quantifiedInPredicate3653);
			columnReference117=columnReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_columnReference.add(columnReference117.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:653:29: ( NOT )?
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( (LA35_0==NOT) ) {
				alt35=1;
			}
			switch (alt35) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:653:29: NOT
					{
					NOT118=(Token)match(input,NOT,FOLLOW_NOT_in_quantifiedInPredicate3655); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT118);

					}
					break;

			}

			IN119=(Token)match(input,IN,FOLLOW_IN_in_quantifiedInPredicate3658); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_IN.add(IN119);

			LPAREN120=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_quantifiedInPredicate3660); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN120);

			pushFollow(FOLLOW_inValueList_in_quantifiedInPredicate3662);
			inValueList121=inValueList();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_inValueList.add(inValueList121.getTree());
			RPAREN122=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_quantifiedInPredicate3664); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN122);

			// AST REWRITE
			// elements: NOT, ANY, inValueList, columnReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 654:17: -> ^( PRED_IN ANY columnReference inValueList ( NOT )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:655:25: ^( PRED_IN ANY columnReference inValueList ( NOT )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_IN, "PRED_IN"), root_1);
				adaptor.addChild(root_1, stream_ANY.nextNode());
				adaptor.addChild(root_1, stream_columnReference.nextTree());
				adaptor.addChild(root_1, stream_inValueList.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:655:67: ( NOT )?
				if ( stream_NOT.hasNext() ) {
					adaptor.addChild(root_1, stream_NOT.nextNode());
				}
				stream_NOT.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "quantifiedInPredicate"


	public static class textSearchPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "textSearchPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:658:1: textSearchPredicate : CONTAINS LPAREN ( qualifier COMMA )? textSearchExpression RPAREN -> ^( PRED_FTS textSearchExpression ( qualifier )? ) ;
	public final CMISParser.textSearchPredicate_return textSearchPredicate() throws RecognitionException {
		CMISParser.textSearchPredicate_return retval = new CMISParser.textSearchPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token CONTAINS123=null;
		Token LPAREN124=null;
		Token COMMA126=null;
		Token RPAREN128=null;
		ParserRuleReturnScope qualifier125 =null;
		ParserRuleReturnScope textSearchExpression127 =null;

		Object CONTAINS123_tree=null;
		Object LPAREN124_tree=null;
		Object COMMA126_tree=null;
		Object RPAREN128_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_CONTAINS=new RewriteRuleTokenStream(adaptor,"token CONTAINS");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_qualifier=new RewriteRuleSubtreeStream(adaptor,"rule qualifier");
		RewriteRuleSubtreeStream stream_textSearchExpression=new RewriteRuleSubtreeStream(adaptor,"rule textSearchExpression");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:659:9: ( CONTAINS LPAREN ( qualifier COMMA )? textSearchExpression RPAREN -> ^( PRED_FTS textSearchExpression ( qualifier )? ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:660:9: CONTAINS LPAREN ( qualifier COMMA )? textSearchExpression RPAREN
			{
			CONTAINS123=(Token)match(input,CONTAINS,FOLLOW_CONTAINS_in_textSearchPredicate3752); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CONTAINS.add(CONTAINS123);

			LPAREN124=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_textSearchPredicate3754); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN124);

			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:660:25: ( qualifier COMMA )?
			int alt36=2;
			int LA36_0 = input.LA(1);
			if ( (LA36_0==ID) ) {
				alt36=1;
			}
			else if ( (LA36_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt36=1;
			}
			switch (alt36) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:660:26: qualifier COMMA
					{
					pushFollow(FOLLOW_qualifier_in_textSearchPredicate3757);
					qualifier125=qualifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qualifier.add(qualifier125.getTree());
					COMMA126=(Token)match(input,COMMA,FOLLOW_COMMA_in_textSearchPredicate3759); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA126);

					}
					break;

			}

			pushFollow(FOLLOW_textSearchExpression_in_textSearchPredicate3763);
			textSearchExpression127=textSearchExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_textSearchExpression.add(textSearchExpression127.getTree());
			RPAREN128=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_textSearchPredicate3765); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN128);

			// AST REWRITE
			// elements: textSearchExpression, qualifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 661:17: -> ^( PRED_FTS textSearchExpression ( qualifier )? )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:662:25: ^( PRED_FTS textSearchExpression ( qualifier )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_FTS, "PRED_FTS"), root_1);
				adaptor.addChild(root_1, stream_textSearchExpression.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:662:57: ( qualifier )?
				if ( stream_qualifier.hasNext() ) {
					adaptor.addChild(root_1, stream_qualifier.nextTree());
				}
				stream_qualifier.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "textSearchPredicate"


	public static class folderPredicate_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "folderPredicate"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:665:1: folderPredicate : ( IN_FOLDER folderPredicateArgs -> ^( PRED_CHILD folderPredicateArgs ) | IN_TREE folderPredicateArgs -> ^( PRED_DESCENDANT folderPredicateArgs ) );
	public final CMISParser.folderPredicate_return folderPredicate() throws RecognitionException {
		CMISParser.folderPredicate_return retval = new CMISParser.folderPredicate_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token IN_FOLDER129=null;
		Token IN_TREE131=null;
		ParserRuleReturnScope folderPredicateArgs130 =null;
		ParserRuleReturnScope folderPredicateArgs132 =null;

		Object IN_FOLDER129_tree=null;
		Object IN_TREE131_tree=null;
		RewriteRuleTokenStream stream_IN_TREE=new RewriteRuleTokenStream(adaptor,"token IN_TREE");
		RewriteRuleTokenStream stream_IN_FOLDER=new RewriteRuleTokenStream(adaptor,"token IN_FOLDER");
		RewriteRuleSubtreeStream stream_folderPredicateArgs=new RewriteRuleSubtreeStream(adaptor,"rule folderPredicateArgs");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:666:9: ( IN_FOLDER folderPredicateArgs -> ^( PRED_CHILD folderPredicateArgs ) | IN_TREE folderPredicateArgs -> ^( PRED_DESCENDANT folderPredicateArgs ) )
			int alt37=2;
			int LA37_0 = input.LA(1);
			if ( (LA37_0==IN_FOLDER) ) {
				alt37=1;
			}
			else if ( (LA37_0==IN_TREE) ) {
				alt37=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 37, 0, input);
				throw nvae;
			}

			switch (alt37) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:667:9: IN_FOLDER folderPredicateArgs
					{
					IN_FOLDER129=(Token)match(input,IN_FOLDER,FOLLOW_IN_FOLDER_in_folderPredicate3849); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IN_FOLDER.add(IN_FOLDER129);

					pushFollow(FOLLOW_folderPredicateArgs_in_folderPredicate3851);
					folderPredicateArgs130=folderPredicateArgs();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_folderPredicateArgs.add(folderPredicateArgs130.getTree());
					// AST REWRITE
					// elements: folderPredicateArgs
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 668:17: -> ^( PRED_CHILD folderPredicateArgs )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:669:25: ^( PRED_CHILD folderPredicateArgs )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_CHILD, "PRED_CHILD"), root_1);
						adaptor.addChild(root_1, stream_folderPredicateArgs.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:670:11: IN_TREE folderPredicateArgs
					{
					IN_TREE131=(Token)match(input,IN_TREE,FOLLOW_IN_TREE_in_folderPredicate3911); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IN_TREE.add(IN_TREE131);

					pushFollow(FOLLOW_folderPredicateArgs_in_folderPredicate3913);
					folderPredicateArgs132=folderPredicateArgs();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_folderPredicateArgs.add(folderPredicateArgs132.getTree());
					// AST REWRITE
					// elements: folderPredicateArgs
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 671:17: -> ^( PRED_DESCENDANT folderPredicateArgs )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:672:25: ^( PRED_DESCENDANT folderPredicateArgs )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PRED_DESCENDANT, "PRED_DESCENDANT"), root_1);
						adaptor.addChild(root_1, stream_folderPredicateArgs.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "folderPredicate"


	public static class folderPredicateArgs_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "folderPredicateArgs"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:675:1: folderPredicateArgs : LPAREN ( qualifier COMMA )? folderId RPAREN -> folderId ( qualifier )? ;
	public final CMISParser.folderPredicateArgs_return folderPredicateArgs() throws RecognitionException {
		CMISParser.folderPredicateArgs_return retval = new CMISParser.folderPredicateArgs_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN133=null;
		Token COMMA135=null;
		Token RPAREN137=null;
		ParserRuleReturnScope qualifier134 =null;
		ParserRuleReturnScope folderId136 =null;

		Object LPAREN133_tree=null;
		Object COMMA135_tree=null;
		Object RPAREN137_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_qualifier=new RewriteRuleSubtreeStream(adaptor,"rule qualifier");
		RewriteRuleSubtreeStream stream_folderId=new RewriteRuleSubtreeStream(adaptor,"rule folderId");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:676:9: ( LPAREN ( qualifier COMMA )? folderId RPAREN -> folderId ( qualifier )? )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:677:9: LPAREN ( qualifier COMMA )? folderId RPAREN
			{
			LPAREN133=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_folderPredicateArgs3994); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN133);

			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:677:16: ( qualifier COMMA )?
			int alt38=2;
			int LA38_0 = input.LA(1);
			if ( (LA38_0==ID) ) {
				alt38=1;
			}
			else if ( (LA38_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt38=1;
			}
			switch (alt38) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:677:17: qualifier COMMA
					{
					pushFollow(FOLLOW_qualifier_in_folderPredicateArgs3997);
					qualifier134=qualifier();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qualifier.add(qualifier134.getTree());
					COMMA135=(Token)match(input,COMMA,FOLLOW_COMMA_in_folderPredicateArgs3999); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA135);

					}
					break;

			}

			pushFollow(FOLLOW_folderId_in_folderPredicateArgs4003);
			folderId136=folderId();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_folderId.add(folderId136.getTree());
			RPAREN137=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_folderPredicateArgs4005); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN137);

			// AST REWRITE
			// elements: folderId, qualifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 678:17: -> folderId ( qualifier )?
			{
				adaptor.addChild(root_0, stream_folderId.nextTree());
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:678:29: ( qualifier )?
				if ( stream_qualifier.hasNext() ) {
					adaptor.addChild(root_0, stream_qualifier.nextTree());
				}
				stream_qualifier.reset();

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "folderPredicateArgs"


	public static class orderByClause_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "orderByClause"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:681:1: orderByClause : ORDER BY sortSpecification ( COMMA sortSpecification )* -> ^( ORDER ( sortSpecification )+ ) ;
	public final CMISParser.orderByClause_return orderByClause() throws RecognitionException {
		CMISParser.orderByClause_return retval = new CMISParser.orderByClause_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ORDER138=null;
		Token BY139=null;
		Token COMMA141=null;
		ParserRuleReturnScope sortSpecification140 =null;
		ParserRuleReturnScope sortSpecification142 =null;

		Object ORDER138_tree=null;
		Object BY139_tree=null;
		Object COMMA141_tree=null;
		RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
		RewriteRuleTokenStream stream_ORDER=new RewriteRuleTokenStream(adaptor,"token ORDER");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_sortSpecification=new RewriteRuleSubtreeStream(adaptor,"rule sortSpecification");


		    paraphrases.push("in order by"); 

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:690:9: ( ORDER BY sortSpecification ( COMMA sortSpecification )* -> ^( ORDER ( sortSpecification )+ ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:691:9: ORDER BY sortSpecification ( COMMA sortSpecification )*
			{
			ORDER138=(Token)match(input,ORDER,FOLLOW_ORDER_in_orderByClause4071); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ORDER.add(ORDER138);

			BY139=(Token)match(input,BY,FOLLOW_BY_in_orderByClause4073); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_BY.add(BY139);

			pushFollow(FOLLOW_sortSpecification_in_orderByClause4075);
			sortSpecification140=sortSpecification();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_sortSpecification.add(sortSpecification140.getTree());
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:691:36: ( COMMA sortSpecification )*
			loop39:
			while (true) {
				int alt39=2;
				int LA39_0 = input.LA(1);
				if ( (LA39_0==COMMA) ) {
					alt39=1;
				}

				switch (alt39) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:691:37: COMMA sortSpecification
					{
					COMMA141=(Token)match(input,COMMA,FOLLOW_COMMA_in_orderByClause4078); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA141);

					pushFollow(FOLLOW_sortSpecification_in_orderByClause4080);
					sortSpecification142=sortSpecification();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_sortSpecification.add(sortSpecification142.getTree());
					}
					break;

				default :
					break loop39;
				}
			}

			// AST REWRITE
			// elements: sortSpecification, ORDER
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 692:17: -> ^( ORDER ( sortSpecification )+ )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:693:25: ^( ORDER ( sortSpecification )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot(stream_ORDER.nextNode(), root_1);
				if ( !(stream_sortSpecification.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_sortSpecification.hasNext() ) {
					adaptor.addChild(root_1, stream_sortSpecification.nextTree());
				}
				stream_sortSpecification.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) {
			    paraphrases.pop(); 
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "orderByClause"


	public static class sortSpecification_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "sortSpecification"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:696:1: sortSpecification : ( columnReference -> ^( SORT_SPECIFICATION columnReference ASC ) | columnReference (by= ASC |by= DESC ) -> ^( SORT_SPECIFICATION columnReference $by) );
	public final CMISParser.sortSpecification_return sortSpecification() throws RecognitionException {
		CMISParser.sortSpecification_return retval = new CMISParser.sortSpecification_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token by=null;
		ParserRuleReturnScope columnReference143 =null;
		ParserRuleReturnScope columnReference144 =null;

		Object by_tree=null;
		RewriteRuleTokenStream stream_ASC=new RewriteRuleTokenStream(adaptor,"token ASC");
		RewriteRuleTokenStream stream_DESC=new RewriteRuleTokenStream(adaptor,"token DESC");
		RewriteRuleSubtreeStream stream_columnReference=new RewriteRuleSubtreeStream(adaptor,"rule columnReference");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:697:9: ( columnReference -> ^( SORT_SPECIFICATION columnReference ASC ) | columnReference (by= ASC |by= DESC ) -> ^( SORT_SPECIFICATION columnReference $by) )
			int alt41=2;
			alt41 = dfa41.predict(input);
			switch (alt41) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:698:9: columnReference
					{
					pushFollow(FOLLOW_columnReference_in_sortSpecification4164);
					columnReference143=columnReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnReference.add(columnReference143.getTree());
					// AST REWRITE
					// elements: columnReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 699:17: -> ^( SORT_SPECIFICATION columnReference ASC )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:700:25: ^( SORT_SPECIFICATION columnReference ASC )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SORT_SPECIFICATION, "SORT_SPECIFICATION"), root_1);
						adaptor.addChild(root_1, stream_columnReference.nextTree());
						adaptor.addChild(root_1, (Object)adaptor.create(ASC, "ASC"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:701:11: columnReference (by= ASC |by= DESC )
					{
					pushFollow(FOLLOW_columnReference_in_sortSpecification4226);
					columnReference144=columnReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_columnReference.add(columnReference144.getTree());
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:702:9: (by= ASC |by= DESC )
					int alt40=2;
					int LA40_0 = input.LA(1);
					if ( (LA40_0==ASC) ) {
						alt40=1;
					}
					else if ( (LA40_0==DESC) ) {
						alt40=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 40, 0, input);
						throw nvae;
					}

					switch (alt40) {
						case 1 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:703:17: by= ASC
							{
							by=(Token)match(input,ASC,FOLLOW_ASC_in_sortSpecification4256); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_ASC.add(by);

							}
							break;
						case 2 :
							// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:704:19: by= DESC
							{
							by=(Token)match(input,DESC,FOLLOW_DESC_in_sortSpecification4278); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DESC.add(by);

							}
							break;

					}

					// AST REWRITE
					// elements: by, columnReference
					// token labels: by
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_by=new RewriteRuleTokenStream(adaptor,"token by",by);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 706:17: -> ^( SORT_SPECIFICATION columnReference $by)
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:707:25: ^( SORT_SPECIFICATION columnReference $by)
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SORT_SPECIFICATION, "SORT_SPECIFICATION"), root_1);
						adaptor.addChild(root_1, stream_columnReference.nextTree());
						adaptor.addChild(root_1, stream_by.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "sortSpecification"


	public static class correlationName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "correlationName"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:710:1: correlationName : identifier ;
	public final CMISParser.correlationName_return correlationName() throws RecognitionException {
		CMISParser.correlationName_return retval = new CMISParser.correlationName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope identifier145 =null;


		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:711:9: ( identifier )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:712:9: identifier
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_identifier_in_correlationName4372);
			identifier145=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, identifier145.getTree());

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "correlationName"


	public static class tableName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "tableName"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:720:1: tableName : identifier -> identifier ;
	public final CMISParser.tableName_return tableName() throws RecognitionException {
		CMISParser.tableName_return retval = new CMISParser.tableName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope identifier146 =null;

		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:721:9: ( identifier -> identifier )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:722:9: identifier
			{
			pushFollow(FOLLOW_identifier_in_tableName4408);
			identifier146=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier146.getTree());
			// AST REWRITE
			// elements: identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 723:17: -> identifier
			{
				adaptor.addChild(root_0, stream_identifier.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "tableName"


	public static class columnName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "columnName"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:726:1: columnName : identifier -> identifier ;
	public final CMISParser.columnName_return columnName() throws RecognitionException {
		CMISParser.columnName_return retval = new CMISParser.columnName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope identifier147 =null;

		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:727:9: ( identifier -> identifier )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:728:9: identifier
			{
			pushFollow(FOLLOW_identifier_in_columnName4461);
			identifier147=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier147.getTree());
			// AST REWRITE
			// elements: identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 729:17: -> identifier
			{
				adaptor.addChild(root_0, stream_identifier.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "columnName"


	public static class parameterName_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "parameterName"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:732:1: parameterName : COLON identifier -> ^( PARAMETER identifier ) ;
	public final CMISParser.parameterName_return parameterName() throws RecognitionException {
		CMISParser.parameterName_return retval = new CMISParser.parameterName_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COLON148=null;
		ParserRuleReturnScope identifier149 =null;

		Object COLON148_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:733:9: ( COLON identifier -> ^( PARAMETER identifier ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:734:9: COLON identifier
			{
			COLON148=(Token)match(input,COLON,FOLLOW_COLON_in_parameterName4514); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_COLON.add(COLON148);

			pushFollow(FOLLOW_identifier_in_parameterName4516);
			identifier149=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier149.getTree());
			// AST REWRITE
			// elements: identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 735:17: -> ^( PARAMETER identifier )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:736:25: ^( PARAMETER identifier )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAMETER, "PARAMETER"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "parameterName"


	public static class folderId_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "folderId"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:739:1: folderId : characterStringLiteral -> characterStringLiteral ;
	public final CMISParser.folderId_return folderId() throws RecognitionException {
		CMISParser.folderId_return retval = new CMISParser.folderId_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope characterStringLiteral150 =null;

		RewriteRuleSubtreeStream stream_characterStringLiteral=new RewriteRuleSubtreeStream(adaptor,"rule characterStringLiteral");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:740:9: ( characterStringLiteral -> characterStringLiteral )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:741:9: characterStringLiteral
			{
			pushFollow(FOLLOW_characterStringLiteral_in_folderId4597);
			characterStringLiteral150=characterStringLiteral();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_characterStringLiteral.add(characterStringLiteral150.getTree());
			// AST REWRITE
			// elements: characterStringLiteral
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 742:17: -> characterStringLiteral
			{
				adaptor.addChild(root_0, stream_characterStringLiteral.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "folderId"


	public static class textSearchExpression_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "textSearchExpression"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:745:1: textSearchExpression : QUOTED_STRING ;
	public final CMISParser.textSearchExpression_return textSearchExpression() throws RecognitionException {
		CMISParser.textSearchExpression_return retval = new CMISParser.textSearchExpression_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token QUOTED_STRING151=null;

		Object QUOTED_STRING151_tree=null;

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:746:9: ( QUOTED_STRING )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:747:9: QUOTED_STRING
			{
			root_0 = (Object)adaptor.nil();


			QUOTED_STRING151=(Token)match(input,QUOTED_STRING,FOLLOW_QUOTED_STRING_in_textSearchExpression4650); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			QUOTED_STRING151_tree = (Object)adaptor.create(QUOTED_STRING151);
			adaptor.addChild(root_0, QUOTED_STRING151_tree);
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "textSearchExpression"


	public static class identifier_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "identifier"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:750:1: identifier : ( ID -> ID |{...}? => DOUBLE_QUOTE keyWordOrId DOUBLE_QUOTE -> ^( keyWordOrId ) );
	public final CMISParser.identifier_return identifier() throws RecognitionException {
		CMISParser.identifier_return retval = new CMISParser.identifier_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ID152=null;
		Token DOUBLE_QUOTE153=null;
		Token DOUBLE_QUOTE155=null;
		ParserRuleReturnScope keyWordOrId154 =null;

		Object ID152_tree=null;
		Object DOUBLE_QUOTE153_tree=null;
		Object DOUBLE_QUOTE155_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_DOUBLE_QUOTE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_QUOTE");
		RewriteRuleSubtreeStream stream_keyWordOrId=new RewriteRuleSubtreeStream(adaptor,"rule keyWordOrId");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:751:9: ( ID -> ID |{...}? => DOUBLE_QUOTE keyWordOrId DOUBLE_QUOTE -> ^( keyWordOrId ) )
			int alt42=2;
			int LA42_0 = input.LA(1);
			if ( (LA42_0==ID) ) {
				alt42=1;
			}
			else if ( (LA42_0==DOUBLE_QUOTE) && ((strict == false))) {
				alt42=2;
			}

			switch (alt42) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:752:9: ID
					{
					ID152=(Token)match(input,ID,FOLLOW_ID_in_identifier4683); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID152);

					// AST REWRITE
					// elements: ID
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 753:17: -> ID
					{
						adaptor.addChild(root_0, stream_ID.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:754:11: {...}? => DOUBLE_QUOTE keyWordOrId DOUBLE_QUOTE
					{
					if ( !((strict == false)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "identifier", "strict == false");
					}
					DOUBLE_QUOTE153=(Token)match(input,DOUBLE_QUOTE,FOLLOW_DOUBLE_QUOTE_in_identifier4718); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOUBLE_QUOTE.add(DOUBLE_QUOTE153);

					pushFollow(FOLLOW_keyWordOrId_in_identifier4720);
					keyWordOrId154=keyWordOrId();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_keyWordOrId.add(keyWordOrId154.getTree());
					DOUBLE_QUOTE155=(Token)match(input,DOUBLE_QUOTE,FOLLOW_DOUBLE_QUOTE_in_identifier4722); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOUBLE_QUOTE.add(DOUBLE_QUOTE155);

					// AST REWRITE
					// elements: keyWordOrId
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 755:17: -> ^( keyWordOrId )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:756:25: ^( keyWordOrId )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot(stream_keyWordOrId.nextNode(), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "identifier"


	public static class signedNumericLiteral_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "signedNumericLiteral"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:759:1: signedNumericLiteral : ( FLOATING_POINT_LITERAL -> ^( NUMERIC_LITERAL FLOATING_POINT_LITERAL ) | integerLiteral -> integerLiteral );
	public final CMISParser.signedNumericLiteral_return signedNumericLiteral() throws RecognitionException {
		CMISParser.signedNumericLiteral_return retval = new CMISParser.signedNumericLiteral_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FLOATING_POINT_LITERAL156=null;
		ParserRuleReturnScope integerLiteral157 =null;

		Object FLOATING_POINT_LITERAL156_tree=null;
		RewriteRuleTokenStream stream_FLOATING_POINT_LITERAL=new RewriteRuleTokenStream(adaptor,"token FLOATING_POINT_LITERAL");
		RewriteRuleSubtreeStream stream_integerLiteral=new RewriteRuleSubtreeStream(adaptor,"rule integerLiteral");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:760:9: ( FLOATING_POINT_LITERAL -> ^( NUMERIC_LITERAL FLOATING_POINT_LITERAL ) | integerLiteral -> integerLiteral )
			int alt43=2;
			int LA43_0 = input.LA(1);
			if ( (LA43_0==FLOATING_POINT_LITERAL) ) {
				alt43=1;
			}
			else if ( (LA43_0==DECIMAL_INTEGER_LITERAL) ) {
				alt43=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 43, 0, input);
				throw nvae;
			}

			switch (alt43) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:761:9: FLOATING_POINT_LITERAL
					{
					FLOATING_POINT_LITERAL156=(Token)match(input,FLOATING_POINT_LITERAL,FOLLOW_FLOATING_POINT_LITERAL_in_signedNumericLiteral4801); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FLOATING_POINT_LITERAL.add(FLOATING_POINT_LITERAL156);

					// AST REWRITE
					// elements: FLOATING_POINT_LITERAL
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 762:17: -> ^( NUMERIC_LITERAL FLOATING_POINT_LITERAL )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:763:25: ^( NUMERIC_LITERAL FLOATING_POINT_LITERAL )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMERIC_LITERAL, "NUMERIC_LITERAL"), root_1);
						adaptor.addChild(root_1, stream_FLOATING_POINT_LITERAL.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:764:11: integerLiteral
					{
					pushFollow(FOLLOW_integerLiteral_in_signedNumericLiteral4861);
					integerLiteral157=integerLiteral();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_integerLiteral.add(integerLiteral157.getTree());
					// AST REWRITE
					// elements: integerLiteral
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 765:17: -> integerLiteral
					{
						adaptor.addChild(root_0, stream_integerLiteral.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "signedNumericLiteral"


	public static class integerLiteral_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "integerLiteral"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:768:1: integerLiteral : DECIMAL_INTEGER_LITERAL -> ^( NUMERIC_LITERAL DECIMAL_INTEGER_LITERAL ) ;
	public final CMISParser.integerLiteral_return integerLiteral() throws RecognitionException {
		CMISParser.integerLiteral_return retval = new CMISParser.integerLiteral_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DECIMAL_INTEGER_LITERAL158=null;

		Object DECIMAL_INTEGER_LITERAL158_tree=null;
		RewriteRuleTokenStream stream_DECIMAL_INTEGER_LITERAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL_INTEGER_LITERAL");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:769:9: ( DECIMAL_INTEGER_LITERAL -> ^( NUMERIC_LITERAL DECIMAL_INTEGER_LITERAL ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:770:9: DECIMAL_INTEGER_LITERAL
			{
			DECIMAL_INTEGER_LITERAL158=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_integerLiteral4914); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL158);

			// AST REWRITE
			// elements: DECIMAL_INTEGER_LITERAL
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 771:17: -> ^( NUMERIC_LITERAL DECIMAL_INTEGER_LITERAL )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:772:25: ^( NUMERIC_LITERAL DECIMAL_INTEGER_LITERAL )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMERIC_LITERAL, "NUMERIC_LITERAL"), root_1);
				adaptor.addChild(root_1, stream_DECIMAL_INTEGER_LITERAL.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "integerLiteral"


	public static class booleanLiteral_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "booleanLiteral"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:775:1: booleanLiteral : ( TRUE -> ^( BOOLEAN_LITERAL TRUE ) | FALSE -> ^( BOOLEAN_LITERAL FALSE ) );
	public final CMISParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
		CMISParser.booleanLiteral_return retval = new CMISParser.booleanLiteral_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TRUE159=null;
		Token FALSE160=null;

		Object TRUE159_tree=null;
		Object FALSE160_tree=null;
		RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
		RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:776:9: ( TRUE -> ^( BOOLEAN_LITERAL TRUE ) | FALSE -> ^( BOOLEAN_LITERAL FALSE ) )
			int alt44=2;
			int LA44_0 = input.LA(1);
			if ( (LA44_0==TRUE) ) {
				alt44=1;
			}
			else if ( (LA44_0==FALSE) ) {
				alt44=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}

			switch (alt44) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:777:9: TRUE
					{
					TRUE159=(Token)match(input,TRUE,FOLLOW_TRUE_in_booleanLiteral4995); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TRUE.add(TRUE159);

					// AST REWRITE
					// elements: TRUE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 778:17: -> ^( BOOLEAN_LITERAL TRUE )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:779:25: ^( BOOLEAN_LITERAL TRUE )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BOOLEAN_LITERAL, "BOOLEAN_LITERAL"), root_1);
						adaptor.addChild(root_1, stream_TRUE.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:780:11: FALSE
					{
					FALSE160=(Token)match(input,FALSE,FOLLOW_FALSE_in_booleanLiteral5055); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FALSE.add(FALSE160);

					// AST REWRITE
					// elements: FALSE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 781:17: -> ^( BOOLEAN_LITERAL FALSE )
					{
						// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:782:25: ^( BOOLEAN_LITERAL FALSE )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BOOLEAN_LITERAL, "BOOLEAN_LITERAL"), root_1);
						adaptor.addChild(root_1, stream_FALSE.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "booleanLiteral"


	public static class datetimeLiteral_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "datetimeLiteral"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:785:1: datetimeLiteral : TIMESTAMP QUOTED_STRING -> ^( DATETIME_LITERAL QUOTED_STRING ) ;
	public final CMISParser.datetimeLiteral_return datetimeLiteral() throws RecognitionException {
		CMISParser.datetimeLiteral_return retval = new CMISParser.datetimeLiteral_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TIMESTAMP161=null;
		Token QUOTED_STRING162=null;

		Object TIMESTAMP161_tree=null;
		Object QUOTED_STRING162_tree=null;
		RewriteRuleTokenStream stream_QUOTED_STRING=new RewriteRuleTokenStream(adaptor,"token QUOTED_STRING");
		RewriteRuleTokenStream stream_TIMESTAMP=new RewriteRuleTokenStream(adaptor,"token TIMESTAMP");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:786:9: ( TIMESTAMP QUOTED_STRING -> ^( DATETIME_LITERAL QUOTED_STRING ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:787:9: TIMESTAMP QUOTED_STRING
			{
			TIMESTAMP161=(Token)match(input,TIMESTAMP,FOLLOW_TIMESTAMP_in_datetimeLiteral5136); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TIMESTAMP.add(TIMESTAMP161);

			QUOTED_STRING162=(Token)match(input,QUOTED_STRING,FOLLOW_QUOTED_STRING_in_datetimeLiteral5138); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_QUOTED_STRING.add(QUOTED_STRING162);

			// AST REWRITE
			// elements: QUOTED_STRING
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 788:17: -> ^( DATETIME_LITERAL QUOTED_STRING )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:789:25: ^( DATETIME_LITERAL QUOTED_STRING )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DATETIME_LITERAL, "DATETIME_LITERAL"), root_1);
				adaptor.addChild(root_1, stream_QUOTED_STRING.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "datetimeLiteral"


	public static class characterStringLiteral_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "characterStringLiteral"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:792:1: characterStringLiteral : QUOTED_STRING -> ^( STRING_LITERAL QUOTED_STRING ) ;
	public final CMISParser.characterStringLiteral_return characterStringLiteral() throws RecognitionException {
		CMISParser.characterStringLiteral_return retval = new CMISParser.characterStringLiteral_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token QUOTED_STRING163=null;

		Object QUOTED_STRING163_tree=null;
		RewriteRuleTokenStream stream_QUOTED_STRING=new RewriteRuleTokenStream(adaptor,"token QUOTED_STRING");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:793:9: ( QUOTED_STRING -> ^( STRING_LITERAL QUOTED_STRING ) )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:794:9: QUOTED_STRING
			{
			QUOTED_STRING163=(Token)match(input,QUOTED_STRING,FOLLOW_QUOTED_STRING_in_characterStringLiteral5219); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_QUOTED_STRING.add(QUOTED_STRING163);

			// AST REWRITE
			// elements: QUOTED_STRING
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 795:17: -> ^( STRING_LITERAL QUOTED_STRING )
			{
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:796:25: ^( STRING_LITERAL QUOTED_STRING )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING_LITERAL, "STRING_LITERAL"), root_1);
				adaptor.addChild(root_1, stream_QUOTED_STRING.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "characterStringLiteral"


	public static class keyWord_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "keyWord"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:799:1: keyWord : ( SELECT | AS | FROM | JOIN | INNER | LEFT | OUTER | ON | WHERE | OR | AND | NOT | IN | LIKE | IS | NULL | ANY | CONTAINS | IN_FOLDER | IN_TREE | ORDER | BY | ASC | DESC | TIMESTAMP | TRUE | FALSE | cmisFunction );
	public final CMISParser.keyWord_return keyWord() throws RecognitionException {
		CMISParser.keyWord_return retval = new CMISParser.keyWord_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token SELECT164=null;
		Token AS165=null;
		Token FROM166=null;
		Token JOIN167=null;
		Token INNER168=null;
		Token LEFT169=null;
		Token OUTER170=null;
		Token ON171=null;
		Token WHERE172=null;
		Token OR173=null;
		Token AND174=null;
		Token NOT175=null;
		Token IN176=null;
		Token LIKE177=null;
		Token IS178=null;
		Token NULL179=null;
		Token ANY180=null;
		Token CONTAINS181=null;
		Token IN_FOLDER182=null;
		Token IN_TREE183=null;
		Token ORDER184=null;
		Token BY185=null;
		Token ASC186=null;
		Token DESC187=null;
		Token TIMESTAMP188=null;
		Token TRUE189=null;
		Token FALSE190=null;
		ParserRuleReturnScope cmisFunction191 =null;

		Object SELECT164_tree=null;
		Object AS165_tree=null;
		Object FROM166_tree=null;
		Object JOIN167_tree=null;
		Object INNER168_tree=null;
		Object LEFT169_tree=null;
		Object OUTER170_tree=null;
		Object ON171_tree=null;
		Object WHERE172_tree=null;
		Object OR173_tree=null;
		Object AND174_tree=null;
		Object NOT175_tree=null;
		Object IN176_tree=null;
		Object LIKE177_tree=null;
		Object IS178_tree=null;
		Object NULL179_tree=null;
		Object ANY180_tree=null;
		Object CONTAINS181_tree=null;
		Object IN_FOLDER182_tree=null;
		Object IN_TREE183_tree=null;
		Object ORDER184_tree=null;
		Object BY185_tree=null;
		Object ASC186_tree=null;
		Object DESC187_tree=null;
		Object TIMESTAMP188_tree=null;
		Object TRUE189_tree=null;
		Object FALSE190_tree=null;

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:800:9: ( SELECT | AS | FROM | JOIN | INNER | LEFT | OUTER | ON | WHERE | OR | AND | NOT | IN | LIKE | IS | NULL | ANY | CONTAINS | IN_FOLDER | IN_TREE | ORDER | BY | ASC | DESC | TIMESTAMP | TRUE | FALSE | cmisFunction )
			int alt45=28;
			switch ( input.LA(1) ) {
			case SELECT:
				{
				alt45=1;
				}
				break;
			case AS:
				{
				alt45=2;
				}
				break;
			case FROM:
				{
				alt45=3;
				}
				break;
			case JOIN:
				{
				alt45=4;
				}
				break;
			case INNER:
				{
				alt45=5;
				}
				break;
			case LEFT:
				{
				alt45=6;
				}
				break;
			case OUTER:
				{
				alt45=7;
				}
				break;
			case ON:
				{
				alt45=8;
				}
				break;
			case WHERE:
				{
				alt45=9;
				}
				break;
			case OR:
				{
				alt45=10;
				}
				break;
			case AND:
				{
				alt45=11;
				}
				break;
			case NOT:
				{
				alt45=12;
				}
				break;
			case IN:
				{
				alt45=13;
				}
				break;
			case LIKE:
				{
				alt45=14;
				}
				break;
			case IS:
				{
				alt45=15;
				}
				break;
			case NULL:
				{
				alt45=16;
				}
				break;
			case ANY:
				{
				alt45=17;
				}
				break;
			case CONTAINS:
				{
				alt45=18;
				}
				break;
			case IN_FOLDER:
				{
				alt45=19;
				}
				break;
			case IN_TREE:
				{
				alt45=20;
				}
				break;
			case ORDER:
				{
				alt45=21;
				}
				break;
			case BY:
				{
				alt45=22;
				}
				break;
			case ASC:
				{
				alt45=23;
				}
				break;
			case DESC:
				{
				alt45=24;
				}
				break;
			case TIMESTAMP:
				{
				alt45=25;
				}
				break;
			case TRUE:
				{
				alt45=26;
				}
				break;
			case FALSE:
				{
				alt45=27;
				}
				break;
			case SCORE:
				{
				alt45=28;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 45, 0, input);
				throw nvae;
			}
			switch (alt45) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:801:9: SELECT
					{
					root_0 = (Object)adaptor.nil();


					SELECT164=(Token)match(input,SELECT,FOLLOW_SELECT_in_keyWord5300); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					SELECT164_tree = (Object)adaptor.create(SELECT164);
					adaptor.addChild(root_0, SELECT164_tree);
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:802:11: AS
					{
					root_0 = (Object)adaptor.nil();


					AS165=(Token)match(input,AS,FOLLOW_AS_in_keyWord5312); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AS165_tree = (Object)adaptor.create(AS165);
					adaptor.addChild(root_0, AS165_tree);
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:803:11: FROM
					{
					root_0 = (Object)adaptor.nil();


					FROM166=(Token)match(input,FROM,FOLLOW_FROM_in_keyWord5324); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					FROM166_tree = (Object)adaptor.create(FROM166);
					adaptor.addChild(root_0, FROM166_tree);
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:804:11: JOIN
					{
					root_0 = (Object)adaptor.nil();


					JOIN167=(Token)match(input,JOIN,FOLLOW_JOIN_in_keyWord5336); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					JOIN167_tree = (Object)adaptor.create(JOIN167);
					adaptor.addChild(root_0, JOIN167_tree);
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:805:11: INNER
					{
					root_0 = (Object)adaptor.nil();


					INNER168=(Token)match(input,INNER,FOLLOW_INNER_in_keyWord5348); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					INNER168_tree = (Object)adaptor.create(INNER168);
					adaptor.addChild(root_0, INNER168_tree);
					}

					}
					break;
				case 6 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:806:11: LEFT
					{
					root_0 = (Object)adaptor.nil();


					LEFT169=(Token)match(input,LEFT,FOLLOW_LEFT_in_keyWord5360); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					LEFT169_tree = (Object)adaptor.create(LEFT169);
					adaptor.addChild(root_0, LEFT169_tree);
					}

					}
					break;
				case 7 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:807:11: OUTER
					{
					root_0 = (Object)adaptor.nil();


					OUTER170=(Token)match(input,OUTER,FOLLOW_OUTER_in_keyWord5372); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					OUTER170_tree = (Object)adaptor.create(OUTER170);
					adaptor.addChild(root_0, OUTER170_tree);
					}

					}
					break;
				case 8 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:808:11: ON
					{
					root_0 = (Object)adaptor.nil();


					ON171=(Token)match(input,ON,FOLLOW_ON_in_keyWord5384); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ON171_tree = (Object)adaptor.create(ON171);
					adaptor.addChild(root_0, ON171_tree);
					}

					}
					break;
				case 9 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:809:11: WHERE
					{
					root_0 = (Object)adaptor.nil();


					WHERE172=(Token)match(input,WHERE,FOLLOW_WHERE_in_keyWord5396); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					WHERE172_tree = (Object)adaptor.create(WHERE172);
					adaptor.addChild(root_0, WHERE172_tree);
					}

					}
					break;
				case 10 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:810:11: OR
					{
					root_0 = (Object)adaptor.nil();


					OR173=(Token)match(input,OR,FOLLOW_OR_in_keyWord5408); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					OR173_tree = (Object)adaptor.create(OR173);
					adaptor.addChild(root_0, OR173_tree);
					}

					}
					break;
				case 11 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:811:11: AND
					{
					root_0 = (Object)adaptor.nil();


					AND174=(Token)match(input,AND,FOLLOW_AND_in_keyWord5420); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AND174_tree = (Object)adaptor.create(AND174);
					adaptor.addChild(root_0, AND174_tree);
					}

					}
					break;
				case 12 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:812:11: NOT
					{
					root_0 = (Object)adaptor.nil();


					NOT175=(Token)match(input,NOT,FOLLOW_NOT_in_keyWord5432); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					NOT175_tree = (Object)adaptor.create(NOT175);
					adaptor.addChild(root_0, NOT175_tree);
					}

					}
					break;
				case 13 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:813:11: IN
					{
					root_0 = (Object)adaptor.nil();


					IN176=(Token)match(input,IN,FOLLOW_IN_in_keyWord5444); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IN176_tree = (Object)adaptor.create(IN176);
					adaptor.addChild(root_0, IN176_tree);
					}

					}
					break;
				case 14 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:814:11: LIKE
					{
					root_0 = (Object)adaptor.nil();


					LIKE177=(Token)match(input,LIKE,FOLLOW_LIKE_in_keyWord5456); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					LIKE177_tree = (Object)adaptor.create(LIKE177);
					adaptor.addChild(root_0, LIKE177_tree);
					}

					}
					break;
				case 15 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:815:11: IS
					{
					root_0 = (Object)adaptor.nil();


					IS178=(Token)match(input,IS,FOLLOW_IS_in_keyWord5468); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IS178_tree = (Object)adaptor.create(IS178);
					adaptor.addChild(root_0, IS178_tree);
					}

					}
					break;
				case 16 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:816:11: NULL
					{
					root_0 = (Object)adaptor.nil();


					NULL179=(Token)match(input,NULL,FOLLOW_NULL_in_keyWord5480); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					NULL179_tree = (Object)adaptor.create(NULL179);
					adaptor.addChild(root_0, NULL179_tree);
					}

					}
					break;
				case 17 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:817:11: ANY
					{
					root_0 = (Object)adaptor.nil();


					ANY180=(Token)match(input,ANY,FOLLOW_ANY_in_keyWord5492); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ANY180_tree = (Object)adaptor.create(ANY180);
					adaptor.addChild(root_0, ANY180_tree);
					}

					}
					break;
				case 18 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:818:11: CONTAINS
					{
					root_0 = (Object)adaptor.nil();


					CONTAINS181=(Token)match(input,CONTAINS,FOLLOW_CONTAINS_in_keyWord5504); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CONTAINS181_tree = (Object)adaptor.create(CONTAINS181);
					adaptor.addChild(root_0, CONTAINS181_tree);
					}

					}
					break;
				case 19 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:819:11: IN_FOLDER
					{
					root_0 = (Object)adaptor.nil();


					IN_FOLDER182=(Token)match(input,IN_FOLDER,FOLLOW_IN_FOLDER_in_keyWord5516); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IN_FOLDER182_tree = (Object)adaptor.create(IN_FOLDER182);
					adaptor.addChild(root_0, IN_FOLDER182_tree);
					}

					}
					break;
				case 20 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:820:11: IN_TREE
					{
					root_0 = (Object)adaptor.nil();


					IN_TREE183=(Token)match(input,IN_TREE,FOLLOW_IN_TREE_in_keyWord5528); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IN_TREE183_tree = (Object)adaptor.create(IN_TREE183);
					adaptor.addChild(root_0, IN_TREE183_tree);
					}

					}
					break;
				case 21 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:821:11: ORDER
					{
					root_0 = (Object)adaptor.nil();


					ORDER184=(Token)match(input,ORDER,FOLLOW_ORDER_in_keyWord5540); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ORDER184_tree = (Object)adaptor.create(ORDER184);
					adaptor.addChild(root_0, ORDER184_tree);
					}

					}
					break;
				case 22 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:822:11: BY
					{
					root_0 = (Object)adaptor.nil();


					BY185=(Token)match(input,BY,FOLLOW_BY_in_keyWord5552); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BY185_tree = (Object)adaptor.create(BY185);
					adaptor.addChild(root_0, BY185_tree);
					}

					}
					break;
				case 23 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:823:11: ASC
					{
					root_0 = (Object)adaptor.nil();


					ASC186=(Token)match(input,ASC,FOLLOW_ASC_in_keyWord5564); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ASC186_tree = (Object)adaptor.create(ASC186);
					adaptor.addChild(root_0, ASC186_tree);
					}

					}
					break;
				case 24 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:824:11: DESC
					{
					root_0 = (Object)adaptor.nil();


					DESC187=(Token)match(input,DESC,FOLLOW_DESC_in_keyWord5576); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DESC187_tree = (Object)adaptor.create(DESC187);
					adaptor.addChild(root_0, DESC187_tree);
					}

					}
					break;
				case 25 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:825:11: TIMESTAMP
					{
					root_0 = (Object)adaptor.nil();


					TIMESTAMP188=(Token)match(input,TIMESTAMP,FOLLOW_TIMESTAMP_in_keyWord5588); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TIMESTAMP188_tree = (Object)adaptor.create(TIMESTAMP188);
					adaptor.addChild(root_0, TIMESTAMP188_tree);
					}

					}
					break;
				case 26 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:826:11: TRUE
					{
					root_0 = (Object)adaptor.nil();


					TRUE189=(Token)match(input,TRUE,FOLLOW_TRUE_in_keyWord5600); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRUE189_tree = (Object)adaptor.create(TRUE189);
					adaptor.addChild(root_0, TRUE189_tree);
					}

					}
					break;
				case 27 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:827:11: FALSE
					{
					root_0 = (Object)adaptor.nil();


					FALSE190=(Token)match(input,FALSE,FOLLOW_FALSE_in_keyWord5612); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					FALSE190_tree = (Object)adaptor.create(FALSE190);
					adaptor.addChild(root_0, FALSE190_tree);
					}

					}
					break;
				case 28 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:828:11: cmisFunction
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_cmisFunction_in_keyWord5624);
					cmisFunction191=cmisFunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cmisFunction191.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "keyWord"


	public static class cmisFunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisFunction"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:831:1: cmisFunction : SCORE -> SCORE ;
	public final CMISParser.cmisFunction_return cmisFunction() throws RecognitionException {
		CMISParser.cmisFunction_return retval = new CMISParser.cmisFunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token SCORE192=null;

		Object SCORE192_tree=null;
		RewriteRuleTokenStream stream_SCORE=new RewriteRuleTokenStream(adaptor,"token SCORE");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:832:9: ( SCORE -> SCORE )
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:833:9: SCORE
			{
			SCORE192=(Token)match(input,SCORE,FOLLOW_SCORE_in_cmisFunction5657); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_SCORE.add(SCORE192);

			// AST REWRITE
			// elements: SCORE
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 834:17: -> SCORE
			{
				adaptor.addChild(root_0, stream_SCORE.nextNode());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisFunction"


	public static class keyWordOrId_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "keyWordOrId"
	// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:837:1: keyWordOrId : ( keyWord -> keyWord | ID -> ID );
	public final CMISParser.keyWordOrId_return keyWordOrId() throws RecognitionException {
		CMISParser.keyWordOrId_return retval = new CMISParser.keyWordOrId_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ID194=null;
		ParserRuleReturnScope keyWord193 =null;

		Object ID194_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleSubtreeStream stream_keyWord=new RewriteRuleSubtreeStream(adaptor,"rule keyWord");

		try {
			// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:838:9: ( keyWord -> keyWord | ID -> ID )
			int alt46=2;
			int LA46_0 = input.LA(1);
			if ( ((LA46_0 >= AND && LA46_0 <= ASC)||LA46_0==BY||LA46_0==CONTAINS||LA46_0==DESC||LA46_0==FALSE||LA46_0==FROM||(LA46_0 >= IN && LA46_0 <= LEFT)||LA46_0==LIKE||LA46_0==NOT||LA46_0==NULL||(LA46_0 >= ON && LA46_0 <= OUTER)||(LA46_0 >= SCORE && LA46_0 <= SELECT)||(LA46_0 >= TIMESTAMP && LA46_0 <= WHERE)) ) {
				alt46=1;
			}
			else if ( (LA46_0==ID) ) {
				alt46=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 46, 0, input);
				throw nvae;
			}

			switch (alt46) {
				case 1 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:839:9: keyWord
					{
					pushFollow(FOLLOW_keyWord_in_keyWordOrId5710);
					keyWord193=keyWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_keyWord.add(keyWord193.getTree());
					// AST REWRITE
					// elements: keyWord
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 840:17: -> keyWord
					{
						adaptor.addChild(root_0, stream_keyWord.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:841:11: ID
					{
					ID194=(Token)match(input,ID,FOLLOW_ID_in_keyWordOrId5742); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID194);

					// AST REWRITE
					// elements: ID
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 842:17: -> ID
					{
						adaptor.addChild(root_0, stream_ID.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		    catch(RecognitionException e)
		    {
		        throw new CmisInvalidArgumentException(getErrorString(e), e);
		    }

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "keyWordOrId"

	// $ANTLR start synpred1_CMIS
	public final void synpred1_CMIS_fragment() throws RecognitionException {
		// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:389:9: ( tableName )
		// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:389:10: tableName
		{
		pushFollow(FOLLOW_tableName_in_synpred1_CMIS1324);
		tableName();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_CMIS

	// $ANTLR start synpred2_CMIS
	public final void synpred2_CMIS_fragment() throws RecognitionException {
		// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:9: ( LPAREN singleTable ( joinedTable )+ RPAREN )
		// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:10: LPAREN singleTable ( joinedTable )+ RPAREN
		{
		match(input,LPAREN,FOLLOW_LPAREN_in_synpred2_CMIS1918); if (state.failed) return;

		pushFollow(FOLLOW_singleTable_in_synpred2_CMIS1920);
		singleTable();
		state._fsp--;
		if (state.failed) return;

		// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:29: ( joinedTable )+
		int cnt47=0;
		loop47:
		while (true) {
			int alt47=2;
			int LA47_0 = input.LA(1);
			if ( (LA47_0==INNER||(LA47_0 >= JOIN && LA47_0 <= LEFT)) ) {
				alt47=1;
			}

			switch (alt47) {
			case 1 :
				// W:\\alfresco\\WORK\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\CMIS.g:485:29: joinedTable
				{
				pushFollow(FOLLOW_joinedTable_in_synpred2_CMIS1922);
				joinedTable();
				state._fsp--;
				if (state.failed) return;

				}
				break;

			default :
				if ( cnt47 >= 1 ) break loop47;
				if (state.backtracking>0) {state.failed=true; return;}
				EarlyExitException eee = new EarlyExitException(47, input);
				throw eee;
			}
			cnt47++;
		}

		match(input,RPAREN,FOLLOW_RPAREN_in_synpred2_CMIS1925); if (state.failed) return;

		}

	}
	// $ANTLR end synpred2_CMIS

	// Delegated rules

	public final boolean synpred2_CMIS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_CMIS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_CMIS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_CMIS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA7 dfa7 = new DFA7(this);
	protected DFA9 dfa9 = new DFA9(this);
	protected DFA12 dfa12 = new DFA12(this);
	protected DFA13 dfa13 = new DFA13(this);
	protected DFA26 dfa26 = new DFA26(this);
	protected DFA28 dfa28 = new DFA28(this);
	protected DFA34 dfa34 = new DFA34(this);
	protected DFA41 dfa41 = new DFA41(this);
	static final String DFA7_eotS =
		"\114\uffff";
	static final String DFA7_eofS =
		"\114\uffff";
	static final String DFA7_minS =
		"\1\5\1\7\1\5\44\uffff\35\33\1\7\7\uffff";
	static final String DFA7_maxS =
		"\1\127\1\61\1\127\44\uffff\35\33\1\45\7\uffff";
	static final String DFA7_acceptS =
		"\3\uffff\43\1\1\2\36\uffff\6\1\1\2";
	static final String DFA7_specialS =
		"\1\40\1\15\1\2\44\uffff\1\27\1\25\1\17\1\14\1\13\1\10\1\6\1\5\1\1\1\36"+
		"\1\34\1\31\1\30\1\24\1\22\1\11\1\12\1\16\1\0\1\3\1\7\1\32\1\33\1\35\1"+
		"\37\1\23\1\26\1\4\1\21\1\20\7\uffff}>";
	static final String[] DFA7_transitionS = {
			"\1\16\1\24\1\5\1\32\1\uffff\1\31\6\uffff\1\25\3\uffff\1\33\5\uffff\1"+
			"\2\3\uffff\1\36\1\uffff\1\6\3\uffff\1\1\1\20\1\10\1\26\1\27\1\22\1\7"+
			"\1\11\2\uffff\1\21\5\uffff\1\17\1\uffff\1\23\1\uffff\1\13\1\15\1\30\1"+
			"\12\15\uffff\1\3\1\4\11\uffff\1\34\1\35\1\14",
			"\1\40\7\uffff\1\43\10\uffff\1\37\1\uffff\1\46\1\42\5\uffff\1\44\3\uffff"+
			"\1\41\13\uffff\1\45",
			"\1\61\1\67\1\50\1\75\1\uffff\1\74\6\uffff\1\70\3\uffff\1\76\11\uffff"+
			"\1\101\1\uffff\1\51\3\uffff\1\103\1\63\1\53\1\71\1\72\1\65\1\52\1\54"+
			"\2\uffff\1\64\5\uffff\1\62\1\uffff\1\66\1\uffff\1\56\1\60\1\73\1\55\15"+
			"\uffff\1\102\1\47\11\uffff\1\77\1\100\1\57",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\104",
			"\1\106\7\uffff\1\111\10\uffff\1\105\1\uffff\1\113\1\110\5\uffff\1\112"+
			"\3\uffff\1\107",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
	static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
	static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
	static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
	static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
	static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
	static final short[][] DFA7_transition;

	static {
		int numStates = DFA7_transitionS.length;
		DFA7_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
		}
	}

	protected class DFA7 extends DFA {

		public DFA7(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 7;
			this.eot = DFA7_eot;
			this.eof = DFA7_eof;
			this.min = DFA7_min;
			this.max = DFA7_max;
			this.accept = DFA7_accept;
			this.special = DFA7_special;
			this.transition = DFA7_transition;
		}
		@Override
		public String getDescription() {
			return "319:1: selectSubList : ( valueExpression ( ( AS )? columnName )? -> ^( COLUMN valueExpression ( columnName )? ) | qualifier DOTSTAR -> ^( ALL_COLUMNS qualifier ) );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA7_57 = input.LA(1);
						 
						int index7_57 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_57==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_57);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA7_47 = input.LA(1);
						 
						int index7_47 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_47==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_47);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA7_2 = input.LA(1);
						 
						int index7_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_2==SELECT) && ((strict == false))) {s = 39;}
						else if ( (LA7_2==AS) && ((strict == false))) {s = 40;}
						else if ( (LA7_2==FROM) && ((strict == false))) {s = 41;}
						else if ( (LA7_2==JOIN) && ((strict == false))) {s = 42;}
						else if ( (LA7_2==INNER) && ((strict == false))) {s = 43;}
						else if ( (LA7_2==LEFT) && ((strict == false))) {s = 44;}
						else if ( (LA7_2==OUTER) && ((strict == false))) {s = 45;}
						else if ( (LA7_2==ON) && ((strict == false))) {s = 46;}
						else if ( (LA7_2==WHERE) && ((strict == false))) {s = 47;}
						else if ( (LA7_2==OR) && ((strict == false))) {s = 48;}
						else if ( (LA7_2==AND) && ((strict == false))) {s = 49;}
						else if ( (LA7_2==NOT) && ((strict == false))) {s = 50;}
						else if ( (LA7_2==IN) && ((strict == false))) {s = 51;}
						else if ( (LA7_2==LIKE) && ((strict == false))) {s = 52;}
						else if ( (LA7_2==IS) && ((strict == false))) {s = 53;}
						else if ( (LA7_2==NULL) && ((strict == false))) {s = 54;}
						else if ( (LA7_2==ANY) && ((strict == false))) {s = 55;}
						else if ( (LA7_2==CONTAINS) && ((strict == false))) {s = 56;}
						else if ( (LA7_2==IN_FOLDER) && ((strict == false))) {s = 57;}
						else if ( (LA7_2==IN_TREE) && ((strict == false))) {s = 58;}
						else if ( (LA7_2==ORDER) && ((strict == false))) {s = 59;}
						else if ( (LA7_2==BY) && ((strict == false))) {s = 60;}
						else if ( (LA7_2==ASC) && ((strict == false))) {s = 61;}
						else if ( (LA7_2==DESC) && ((strict == false))) {s = 62;}
						else if ( (LA7_2==TIMESTAMP) && ((strict == false))) {s = 63;}
						else if ( (LA7_2==TRUE) && ((strict == false))) {s = 64;}
						else if ( (LA7_2==FALSE) && ((strict == false))) {s = 65;}
						else if ( (LA7_2==SCORE) && ((strict == false))) {s = 66;}
						else if ( (LA7_2==ID) && ((strict == false))) {s = 67;}
						 
						input.seek(index7_2);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA7_58 = input.LA(1);
						 
						int index7_58 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_58==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_58);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA7_66 = input.LA(1);
						 
						int index7_66 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_66==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_66);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA7_46 = input.LA(1);
						 
						int index7_46 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_46==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_46);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA7_45 = input.LA(1);
						 
						int index7_45 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_45==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_45);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA7_59 = input.LA(1);
						 
						int index7_59 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_59==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_59);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA7_44 = input.LA(1);
						 
						int index7_44 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_44==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_44);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA7_54 = input.LA(1);
						 
						int index7_54 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_54==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_54);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA7_55 = input.LA(1);
						 
						int index7_55 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_55==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_55);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA7_43 = input.LA(1);
						 
						int index7_43 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_43==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_43);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA7_42 = input.LA(1);
						 
						int index7_42 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_42==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_42);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA7_1 = input.LA(1);
						 
						int index7_1 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_1==DOT) ) {s = 31;}
						else if ( (LA7_1==AS) ) {s = 32;}
						else if ( (LA7_1==ID) ) {s = 33;}
						else if ( (LA7_1==DOUBLE_QUOTE) ) {s = 34;}
						else if ( (LA7_1==COMMA) ) {s = 35;}
						else if ( (LA7_1==FROM) ) {s = 36;}
						else if ( (LA7_1==LPAREN) && ((strict == false))) {s = 37;}
						else if ( (LA7_1==DOTSTAR) ) {s = 38;}
						 
						input.seek(index7_1);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA7_56 = input.LA(1);
						 
						int index7_56 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_56==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_56);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA7_41 = input.LA(1);
						 
						int index7_41 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_41==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_41);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA7_68 = input.LA(1);
						 
						int index7_68 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_68==DOT) && ((strict == false))) {s = 69;}
						else if ( (LA7_68==AS) && ((strict == false))) {s = 70;}
						else if ( (LA7_68==ID) && ((strict == false))) {s = 71;}
						else if ( (LA7_68==DOUBLE_QUOTE) && ((strict == false))) {s = 72;}
						else if ( (LA7_68==COMMA) && ((strict == false))) {s = 73;}
						else if ( (LA7_68==FROM) && ((strict == false))) {s = 74;}
						else if ( (LA7_68==DOTSTAR) && ((strict == false))) {s = 75;}
						 
						input.seek(index7_68);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA7_67 = input.LA(1);
						 
						int index7_67 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_67==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_67);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA7_53 = input.LA(1);
						 
						int index7_53 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_53==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_53);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA7_64 = input.LA(1);
						 
						int index7_64 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_64==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_64);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA7_52 = input.LA(1);
						 
						int index7_52 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_52==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_52);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA7_40 = input.LA(1);
						 
						int index7_40 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_40==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_40);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA7_65 = input.LA(1);
						 
						int index7_65 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_65==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_65);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA7_39 = input.LA(1);
						 
						int index7_39 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_39==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_39);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA7_51 = input.LA(1);
						 
						int index7_51 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_51==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_51);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA7_50 = input.LA(1);
						 
						int index7_50 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_50==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_50);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA7_60 = input.LA(1);
						 
						int index7_60 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_60==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_60);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA7_61 = input.LA(1);
						 
						int index7_61 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_61==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_61);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA7_49 = input.LA(1);
						 
						int index7_49 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_49==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_49);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA7_62 = input.LA(1);
						 
						int index7_62 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_62==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_62);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA7_48 = input.LA(1);
						 
						int index7_48 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_48==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_48);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA7_63 = input.LA(1);
						 
						int index7_63 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_63==DOUBLE_QUOTE) && ((strict == false))) {s = 68;}
						 
						input.seek(index7_63);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA7_0 = input.LA(1);
						 
						int index7_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA7_0==ID) ) {s = 1;}
						else if ( (LA7_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						else if ( (LA7_0==SCORE) ) {s = 3;}
						else if ( (LA7_0==SELECT) && ((strict == false))) {s = 4;}
						else if ( (LA7_0==AS) && ((strict == false))) {s = 5;}
						else if ( (LA7_0==FROM) && ((strict == false))) {s = 6;}
						else if ( (LA7_0==JOIN) && ((strict == false))) {s = 7;}
						else if ( (LA7_0==INNER) && ((strict == false))) {s = 8;}
						else if ( (LA7_0==LEFT) && ((strict == false))) {s = 9;}
						else if ( (LA7_0==OUTER) && ((strict == false))) {s = 10;}
						else if ( (LA7_0==ON) && ((strict == false))) {s = 11;}
						else if ( (LA7_0==WHERE) && ((strict == false))) {s = 12;}
						else if ( (LA7_0==OR) && ((strict == false))) {s = 13;}
						else if ( (LA7_0==AND) && ((strict == false))) {s = 14;}
						else if ( (LA7_0==NOT) && ((strict == false))) {s = 15;}
						else if ( (LA7_0==IN) && ((strict == false))) {s = 16;}
						else if ( (LA7_0==LIKE) && ((strict == false))) {s = 17;}
						else if ( (LA7_0==IS) && ((strict == false))) {s = 18;}
						else if ( (LA7_0==NULL) && ((strict == false))) {s = 19;}
						else if ( (LA7_0==ANY) && ((strict == false))) {s = 20;}
						else if ( (LA7_0==CONTAINS) && ((strict == false))) {s = 21;}
						else if ( (LA7_0==IN_FOLDER) && ((strict == false))) {s = 22;}
						else if ( (LA7_0==IN_TREE) && ((strict == false))) {s = 23;}
						else if ( (LA7_0==ORDER) && ((strict == false))) {s = 24;}
						else if ( (LA7_0==BY) && ((strict == false))) {s = 25;}
						else if ( (LA7_0==ASC) && ((strict == false))) {s = 26;}
						else if ( (LA7_0==DESC) && ((strict == false))) {s = 27;}
						else if ( (LA7_0==TIMESTAMP) && ((strict == false))) {s = 28;}
						else if ( (LA7_0==TRUE) && ((strict == false))) {s = 29;}
						else if ( (LA7_0==FALSE) && ((strict == false))) {s = 30;}
						 
						input.seek(index7_0);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 7, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA9_eotS =
		"\121\uffff";
	static final String DFA9_eofS =
		"\1\uffff\1\15\66\uffff\1\103\30\uffff";
	static final String DFA9_minS =
		"\1\33\2\5\30\uffff\35\33\1\5\30\uffff";
	static final String DFA9_maxS =
		"\1\45\2\127\30\uffff\35\33\1\127\30\uffff";
	static final String DFA9_acceptS =
		"\3\uffff\1\1\27\2\36\uffff\1\1\27\2";
	static final String DFA9_specialS =
		"\1\33\1\uffff\1\1\30\uffff\1\15\1\20\1\10\1\11\1\13\1\25\1\27\1\22\1\21"+
		"\1\23\1\34\1\37\1\35\1\30\1\32\1\2\1\3\1\0\1\6\1\4\1\5\1\12\1\7\1\16\1"+
		"\17\1\14\1\24\1\31\1\36\1\26\30\uffff}>";
	static final String[] DFA9_transitionS = {
			"\1\2\11\uffff\1\1",
			"\1\27\1\uffff\1\4\1\31\6\uffff\1\7\5\uffff\1\32\2\uffff\1\3\2\uffff"+
			"\1\6\1\uffff\1\11\3\uffff\1\10\1\uffff\2\12\1\5\1\24\1\17\2\uffff\1\26"+
			"\1\21\1\20\2\12\1\25\5\uffff\1\23\1\12\2\uffff\1\16\1\30\1\14\15\uffff"+
			"\1\22\15\uffff\1\13",
			"\1\45\1\53\1\34\1\61\1\uffff\1\60\6\uffff\1\54\3\uffff\1\62\11\uffff"+
			"\1\65\1\uffff\1\35\3\uffff\1\67\1\47\1\37\1\55\1\56\1\51\1\36\1\40\2"+
			"\uffff\1\50\5\uffff\1\46\1\uffff\1\52\1\uffff\1\42\1\44\1\57\1\41\15"+
			"\uffff\1\66\1\33\11\uffff\1\63\1\64\1\43",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\70",
			"\1\115\1\uffff\1\72\1\117\6\uffff\1\75\5\uffff\1\120\2\uffff\1\71\2"+
			"\uffff\1\74\1\uffff\1\77\3\uffff\1\76\1\uffff\2\100\1\73\1\112\1\105"+
			"\2\uffff\1\114\1\107\1\106\2\100\1\113\5\uffff\1\111\1\100\2\uffff\1"+
			"\104\1\116\1\102\15\uffff\1\110\15\uffff\1\101",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
	static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
	static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
	static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
	static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
	static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
	static final short[][] DFA9_transition;

	static {
		int numStates = DFA9_transitionS.length;
		DFA9_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
		}
	}

	protected class DFA9 extends DFA {

		public DFA9(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 9;
			this.eot = DFA9_eot;
			this.eof = DFA9_eof;
			this.min = DFA9_min;
			this.max = DFA9_max;
			this.accept = DFA9_accept;
			this.special = DFA9_special;
			this.transition = DFA9_transition;
		}
		@Override
		public String getDescription() {
			return "355:9: ( qualifier DOT )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA9_44 = input.LA(1);
						 
						int index9_44 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_44==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_44);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA9_2 = input.LA(1);
						 
						int index9_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_2==SELECT) && ((strict == false))) {s = 27;}
						else if ( (LA9_2==AS) && ((strict == false))) {s = 28;}
						else if ( (LA9_2==FROM) && ((strict == false))) {s = 29;}
						else if ( (LA9_2==JOIN) && ((strict == false))) {s = 30;}
						else if ( (LA9_2==INNER) && ((strict == false))) {s = 31;}
						else if ( (LA9_2==LEFT) && ((strict == false))) {s = 32;}
						else if ( (LA9_2==OUTER) && ((strict == false))) {s = 33;}
						else if ( (LA9_2==ON) && ((strict == false))) {s = 34;}
						else if ( (LA9_2==WHERE) && ((strict == false))) {s = 35;}
						else if ( (LA9_2==OR) && ((strict == false))) {s = 36;}
						else if ( (LA9_2==AND) && ((strict == false))) {s = 37;}
						else if ( (LA9_2==NOT) && ((strict == false))) {s = 38;}
						else if ( (LA9_2==IN) && ((strict == false))) {s = 39;}
						else if ( (LA9_2==LIKE) && ((strict == false))) {s = 40;}
						else if ( (LA9_2==IS) && ((strict == false))) {s = 41;}
						else if ( (LA9_2==NULL) && ((strict == false))) {s = 42;}
						else if ( (LA9_2==ANY) && ((strict == false))) {s = 43;}
						else if ( (LA9_2==CONTAINS) && ((strict == false))) {s = 44;}
						else if ( (LA9_2==IN_FOLDER) && ((strict == false))) {s = 45;}
						else if ( (LA9_2==IN_TREE) && ((strict == false))) {s = 46;}
						else if ( (LA9_2==ORDER) && ((strict == false))) {s = 47;}
						else if ( (LA9_2==BY) && ((strict == false))) {s = 48;}
						else if ( (LA9_2==ASC) && ((strict == false))) {s = 49;}
						else if ( (LA9_2==DESC) && ((strict == false))) {s = 50;}
						else if ( (LA9_2==TIMESTAMP) && ((strict == false))) {s = 51;}
						else if ( (LA9_2==TRUE) && ((strict == false))) {s = 52;}
						else if ( (LA9_2==FALSE) && ((strict == false))) {s = 53;}
						else if ( (LA9_2==SCORE) && ((strict == false))) {s = 54;}
						else if ( (LA9_2==ID) && ((strict == false))) {s = 55;}
						 
						input.seek(index9_2);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA9_42 = input.LA(1);
						 
						int index9_42 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_42==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_42);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA9_43 = input.LA(1);
						 
						int index9_43 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_43==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_43);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA9_46 = input.LA(1);
						 
						int index9_46 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_46==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_46);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA9_47 = input.LA(1);
						 
						int index9_47 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_47==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_47);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA9_45 = input.LA(1);
						 
						int index9_45 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_45==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_45);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA9_49 = input.LA(1);
						 
						int index9_49 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_49==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_49);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA9_29 = input.LA(1);
						 
						int index9_29 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_29==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_29);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA9_30 = input.LA(1);
						 
						int index9_30 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_30==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_30);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA9_48 = input.LA(1);
						 
						int index9_48 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_48==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_48);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA9_31 = input.LA(1);
						 
						int index9_31 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_31==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_31);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA9_52 = input.LA(1);
						 
						int index9_52 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_52==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_52);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA9_27 = input.LA(1);
						 
						int index9_27 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_27==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_27);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA9_50 = input.LA(1);
						 
						int index9_50 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_50==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_50);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA9_51 = input.LA(1);
						 
						int index9_51 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_51==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_51);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA9_28 = input.LA(1);
						 
						int index9_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_28==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_28);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA9_35 = input.LA(1);
						 
						int index9_35 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_35==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_35);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA9_34 = input.LA(1);
						 
						int index9_34 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_34==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_34);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA9_36 = input.LA(1);
						 
						int index9_36 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_36==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_36);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA9_53 = input.LA(1);
						 
						int index9_53 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_53==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_53);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA9_32 = input.LA(1);
						 
						int index9_32 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_32==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_32);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA9_56 = input.LA(1);
						 
						int index9_56 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_56==DOT) && ((strict == false))) {s = 57;}
						else if ( (LA9_56==AS) && ((strict == false))) {s = 58;}
						else if ( (LA9_56==ID) && ((strict == false))) {s = 59;}
						else if ( (LA9_56==DOUBLE_QUOTE) && ((strict == false))) {s = 60;}
						else if ( (LA9_56==COMMA) && ((strict == false))) {s = 61;}
						else if ( (LA9_56==FROM) && ((strict == false))) {s = 62;}
						else if ( (LA9_56==EQUALS) && ((strict == false))) {s = 63;}
						else if ( ((LA9_56 >= GREATERTHAN && LA9_56 <= GREATERTHANOREQUALS)||(LA9_56 >= LESSTHAN && LA9_56 <= LESSTHANOREQUALS)||LA9_56==NOTEQUALS) && ((strict == false))) {s = 64;}
						else if ( (LA9_56==WHERE) && ((strict == false))) {s = 65;}
						else if ( (LA9_56==ORDER) && ((strict == false))) {s = 66;}
						else if ( (LA9_56==EOF) && ((strict == false))) {s = 67;}
						else if ( (LA9_56==ON) && ((strict == false))) {s = 68;}
						else if ( (LA9_56==INNER) && ((strict == false))) {s = 69;}
						else if ( (LA9_56==LEFT) && ((strict == false))) {s = 70;}
						else if ( (LA9_56==JOIN) && ((strict == false))) {s = 71;}
						else if ( (LA9_56==RPAREN) && ((strict == false))) {s = 72;}
						else if ( (LA9_56==NOT) && ((strict == false))) {s = 73;}
						else if ( (LA9_56==IN) && ((strict == false))) {s = 74;}
						else if ( (LA9_56==LIKE) && ((strict == false))) {s = 75;}
						else if ( (LA9_56==IS) && ((strict == false))) {s = 76;}
						else if ( (LA9_56==AND) && ((strict == false))) {s = 77;}
						else if ( (LA9_56==OR) && ((strict == false))) {s = 78;}
						else if ( (LA9_56==ASC) && ((strict == false))) {s = 79;}
						else if ( (LA9_56==DESC) && ((strict == false))) {s = 80;}
						 
						input.seek(index9_56);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA9_33 = input.LA(1);
						 
						int index9_33 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_33==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_33);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA9_40 = input.LA(1);
						 
						int index9_40 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_40==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_40);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA9_54 = input.LA(1);
						 
						int index9_54 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_54==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_54);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA9_41 = input.LA(1);
						 
						int index9_41 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_41==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_41);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA9_0 = input.LA(1);
						 
						int index9_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_0==ID) ) {s = 1;}
						else if ( (LA9_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						 
						input.seek(index9_0);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA9_37 = input.LA(1);
						 
						int index9_37 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_37==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_37);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA9_39 = input.LA(1);
						 
						int index9_39 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_39==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_39);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA9_55 = input.LA(1);
						 
						int index9_55 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_55==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_55);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA9_38 = input.LA(1);
						 
						int index9_38 = input.index();
						input.rewind();
						s = -1;
						if ( (LA9_38==DOUBLE_QUOTE) && ((strict == false))) {s = 56;}
						 
						input.seek(index9_38);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 9, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA12_eotS =
		"\u0096\uffff";
	static final String DFA12_eofS =
		"\u0096\uffff";
	static final String DFA12_minS =
		"\1\5\1\61\34\uffff\2\13\1\5\5\13\1\110\1\33\1\0\1\33\1\0\35\33\5\0\2\13"+
		"\1\5\1\uffff\1\13\1\5\1\13\2\0\35\33\1\0\35\33\1\0\2\13\2\0";
	static final String DFA12_maxS =
		"\1\127\1\61\34\uffff\2\126\1\127\5\126\1\110\1\45\1\0\1\45\1\0\35\33\5"+
		"\0\2\126\1\127\1\uffff\1\126\1\127\1\126\2\0\35\33\1\0\35\33\1\0\2\126"+
		"\2\0";
	static final String DFA12_acceptS =
		"\2\uffff\34\2\62\uffff\1\1\105\uffff";
	static final String DFA12_specialS =
		"\1\15\47\uffff\1\5\1\uffff\1\4\35\uffff\1\6\1\7\1\10\1\11\1\12\7\uffff"+
		"\1\13\1\14\35\uffff\1\3\35\uffff\1\2\2\uffff\1\1\1\0}>";
	static final String[] DFA12_transitionS = {
			"\1\14\1\22\1\3\1\30\1\uffff\1\27\6\uffff\1\23\3\uffff\1\31\11\uffff\1"+
			"\34\1\uffff\1\4\3\uffff\1\35\1\16\1\6\1\24\1\25\1\20\1\5\1\7\2\uffff"+
			"\1\17\5\uffff\1\15\1\uffff\1\21\1\uffff\1\11\1\13\1\26\1\10\15\uffff"+
			"\1\1\1\2\11\uffff\1\32\1\33\1\12",
			"\1\36",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\50\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\4\uffff\1\51\2\uffff\1\40\3\uffff\1\45\1\41\4\uffff"+
			"\1\37\42\uffff\1\43\1\52\13\uffff\1\46\1\44",
			"\1\65\1\73\1\54\1\101\1\uffff\1\100\6\uffff\1\74\3\uffff\1\102\11\uffff"+
			"\1\105\1\uffff\1\55\3\uffff\1\107\1\67\1\57\1\75\1\76\1\71\1\56\1\60"+
			"\2\uffff\1\70\5\uffff\1\66\1\uffff\1\72\1\uffff\1\62\1\64\1\77\1\61\15"+
			"\uffff\1\106\1\53\11\uffff\1\103\1\104\1\63",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\110\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\111\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\112\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\113\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\114\13\uffff\1\46\1\44",
			"\1\115",
			"\1\117\11\uffff\1\116",
			"\1\uffff",
			"\1\122\11\uffff\1\121",
			"\1\uffff",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\123",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\124\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\125\13\uffff\1\46\1\44",
			"\1\140\1\146\1\127\1\154\1\uffff\1\153\6\uffff\1\147\3\uffff\1\155\11"+
			"\uffff\1\160\1\uffff\1\130\3\uffff\1\162\1\142\1\132\1\150\1\151\1\144"+
			"\1\131\1\133\2\uffff\1\143\5\uffff\1\141\1\uffff\1\145\1\uffff\1\135"+
			"\1\137\1\152\1\134\15\uffff\1\161\1\126\11\uffff\1\156\1\157\1\136",
			"",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\163\13\uffff\1\46\1\44",
			"\1\176\1\u0084\1\165\1\u008a\1\uffff\1\u0089\6\uffff\1\u0085\3\uffff"+
			"\1\u008b\11\uffff\1\u008e\1\uffff\1\166\3\uffff\1\u0090\1\u0080\1\170"+
			"\1\u0086\1\u0087\1\u0082\1\167\1\171\2\uffff\1\u0081\5\uffff\1\177\1"+
			"\uffff\1\u0083\1\uffff\1\173\1\175\1\u0088\1\172\15\uffff\1\u008f\1\164"+
			"\11\uffff\1\u008c\1\u008d\1\174",
			"\1\47\7\uffff\1\42\4\uffff\1\51\2\uffff\1\40\3\uffff\1\45\1\41\4\uffff"+
			"\1\37\42\uffff\1\43\1\u0091\13\uffff\1\46\1\44",
			"\1\uffff",
			"\1\uffff",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\u0092",
			"\1\uffff",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\u0093",
			"\1\uffff",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\u0094\13\uffff\1\46\1\44",
			"\1\47\7\uffff\1\42\7\uffff\1\40\3\uffff\1\45\1\41\4\uffff\1\37\42\uffff"+
			"\1\43\1\u0095\13\uffff\1\46\1\44",
			"\1\uffff",
			"\1\uffff"
	};

	static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
	static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
	static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
	static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
	static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
	static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
	static final short[][] DFA12_transition;

	static {
		int numStates = DFA12_transitionS.length;
		DFA12_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
		}
	}

	protected class DFA12 extends DFA {

		public DFA12(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 12;
			this.eot = DFA12_eot;
			this.eof = DFA12_eof;
			this.min = DFA12_min;
			this.max = DFA12_max;
			this.accept = DFA12_accept;
			this.special = DFA12_special;
			this.transition = DFA12_transition;
		}
		@Override
		public String getDescription() {
			return "360:1: valueFunction : (cmisFunctionName= cmisFunction LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $cmisFunctionName LPAREN ( functionArgument )* RPAREN ) |{...}? =>functionName= keyWordOrId LPAREN ( functionArgument )* RPAREN -> ^( FUNCTION $functionName LPAREN ( functionArgument )* RPAREN ) );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA12_149 = input.LA(1);
						 
						int index12_149 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_149);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA12_148 = input.LA(1);
						 
						int index12_148 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_148);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA12_145 = input.LA(1);
						 
						int index12_145 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_145);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA12_115 = input.LA(1);
						 
						int index12_115 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_115);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA12_42 = input.LA(1);
						 
						int index12_42 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_42);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA12_40 = input.LA(1);
						 
						int index12_40 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_40);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA12_72 = input.LA(1);
						 
						int index12_72 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_72);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA12_73 = input.LA(1);
						 
						int index12_73 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_73);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA12_74 = input.LA(1);
						 
						int index12_74 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_74);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA12_75 = input.LA(1);
						 
						int index12_75 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_75);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA12_76 = input.LA(1);
						 
						int index12_76 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_76);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA12_84 = input.LA(1);
						 
						int index12_84 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_84);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA12_85 = input.LA(1);
						 
						int index12_85 = input.index();
						input.rewind();
						s = -1;
						if ( (!(((strict == false)))) ) {s = 80;}
						else if ( ((strict == false)) ) {s = 29;}
						 
						input.seek(index12_85);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA12_0 = input.LA(1);
						 
						int index12_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA12_0==SCORE) ) {s = 1;}
						else if ( (LA12_0==SELECT) && ((strict == false))) {s = 2;}
						else if ( (LA12_0==AS) && ((strict == false))) {s = 3;}
						else if ( (LA12_0==FROM) && ((strict == false))) {s = 4;}
						else if ( (LA12_0==JOIN) && ((strict == false))) {s = 5;}
						else if ( (LA12_0==INNER) && ((strict == false))) {s = 6;}
						else if ( (LA12_0==LEFT) && ((strict == false))) {s = 7;}
						else if ( (LA12_0==OUTER) && ((strict == false))) {s = 8;}
						else if ( (LA12_0==ON) && ((strict == false))) {s = 9;}
						else if ( (LA12_0==WHERE) && ((strict == false))) {s = 10;}
						else if ( (LA12_0==OR) && ((strict == false))) {s = 11;}
						else if ( (LA12_0==AND) && ((strict == false))) {s = 12;}
						else if ( (LA12_0==NOT) && ((strict == false))) {s = 13;}
						else if ( (LA12_0==IN) && ((strict == false))) {s = 14;}
						else if ( (LA12_0==LIKE) && ((strict == false))) {s = 15;}
						else if ( (LA12_0==IS) && ((strict == false))) {s = 16;}
						else if ( (LA12_0==NULL) && ((strict == false))) {s = 17;}
						else if ( (LA12_0==ANY) && ((strict == false))) {s = 18;}
						else if ( (LA12_0==CONTAINS) && ((strict == false))) {s = 19;}
						else if ( (LA12_0==IN_FOLDER) && ((strict == false))) {s = 20;}
						else if ( (LA12_0==IN_TREE) && ((strict == false))) {s = 21;}
						else if ( (LA12_0==ORDER) && ((strict == false))) {s = 22;}
						else if ( (LA12_0==BY) && ((strict == false))) {s = 23;}
						else if ( (LA12_0==ASC) && ((strict == false))) {s = 24;}
						else if ( (LA12_0==DESC) && ((strict == false))) {s = 25;}
						else if ( (LA12_0==TIMESTAMP) && ((strict == false))) {s = 26;}
						else if ( (LA12_0==TRUE) && ((strict == false))) {s = 27;}
						else if ( (LA12_0==FALSE) && ((strict == false))) {s = 28;}
						else if ( (LA12_0==ID) && ((strict == false))) {s = 29;}
						 
						input.seek(index12_0);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 12, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA13_eotS =
		"\76\uffff";
	static final String DFA13_eofS =
		"\76\uffff";
	static final String DFA13_minS =
		"\2\13\1\5\22\uffff\35\33\1\13\13\uffff";
	static final String DFA13_maxS =
		"\2\126\1\127\22\uffff\35\33\1\126\13\uffff";
	static final String DFA13_acceptS =
		"\3\uffff\7\3\1\1\12\2\36\uffff\1\1\12\2";
	static final String DFA13_specialS =
		"\1\7\1\uffff\1\1\22\uffff\1\16\1\21\1\12\1\13\1\14\1\27\1\30\1\24\1\23"+
		"\1\25\1\34\1\37\1\35\1\31\1\33\1\2\1\3\1\0\1\6\1\4\1\5\1\15\1\10\1\20"+
		"\1\22\1\17\1\26\1\32\1\36\1\11\13\uffff}>";
	static final String[] DFA13_transitionS = {
			"\1\11\7\uffff\1\4\7\uffff\1\2\3\uffff\1\7\1\3\4\uffff\1\1\42\uffff\1"+
			"\5\14\uffff\1\10\1\6",
			"\1\24\7\uffff\1\17\4\uffff\1\12\2\uffff\1\15\3\uffff\1\22\1\16\4\uffff"+
			"\1\14\42\uffff\1\20\1\13\13\uffff\1\23\1\21",
			"\1\37\1\45\1\26\1\53\1\uffff\1\52\6\uffff\1\46\3\uffff\1\54\11\uffff"+
			"\1\57\1\uffff\1\27\3\uffff\1\61\1\41\1\31\1\47\1\50\1\43\1\30\1\32\2"+
			"\uffff\1\42\5\uffff\1\40\1\uffff\1\44\1\uffff\1\34\1\36\1\51\1\33\15"+
			"\uffff\1\60\1\25\11\uffff\1\55\1\56\1\35",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\62",
			"\1\75\7\uffff\1\70\4\uffff\1\63\2\uffff\1\66\3\uffff\1\73\1\67\4\uffff"+
			"\1\65\42\uffff\1\71\1\64\13\uffff\1\74\1\72",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
	static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
	static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
	static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
	static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
	static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
	static final short[][] DFA13_transition;

	static {
		int numStates = DFA13_transitionS.length;
		DFA13_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
		}
	}

	protected class DFA13 extends DFA {

		public DFA13(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 13;
			this.eot = DFA13_eot;
			this.eof = DFA13_eof;
			this.min = DFA13_min;
			this.max = DFA13_max;
			this.accept = DFA13_accept;
			this.special = DFA13_special;
			this.transition = DFA13_transition;
		}
		@Override
		public String getDescription() {
			return "378:1: functionArgument : ( qualifier DOT columnName -> ^( COLUMN_REF columnName qualifier ) | identifier | literalOrParameterName );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA13_38 = input.LA(1);
						 
						int index13_38 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_38==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_38);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA13_2 = input.LA(1);
						 
						int index13_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_2==SELECT) && ((strict == false))) {s = 21;}
						else if ( (LA13_2==AS) && ((strict == false))) {s = 22;}
						else if ( (LA13_2==FROM) && ((strict == false))) {s = 23;}
						else if ( (LA13_2==JOIN) && ((strict == false))) {s = 24;}
						else if ( (LA13_2==INNER) && ((strict == false))) {s = 25;}
						else if ( (LA13_2==LEFT) && ((strict == false))) {s = 26;}
						else if ( (LA13_2==OUTER) && ((strict == false))) {s = 27;}
						else if ( (LA13_2==ON) && ((strict == false))) {s = 28;}
						else if ( (LA13_2==WHERE) && ((strict == false))) {s = 29;}
						else if ( (LA13_2==OR) && ((strict == false))) {s = 30;}
						else if ( (LA13_2==AND) && ((strict == false))) {s = 31;}
						else if ( (LA13_2==NOT) && ((strict == false))) {s = 32;}
						else if ( (LA13_2==IN) && ((strict == false))) {s = 33;}
						else if ( (LA13_2==LIKE) && ((strict == false))) {s = 34;}
						else if ( (LA13_2==IS) && ((strict == false))) {s = 35;}
						else if ( (LA13_2==NULL) && ((strict == false))) {s = 36;}
						else if ( (LA13_2==ANY) && ((strict == false))) {s = 37;}
						else if ( (LA13_2==CONTAINS) && ((strict == false))) {s = 38;}
						else if ( (LA13_2==IN_FOLDER) && ((strict == false))) {s = 39;}
						else if ( (LA13_2==IN_TREE) && ((strict == false))) {s = 40;}
						else if ( (LA13_2==ORDER) && ((strict == false))) {s = 41;}
						else if ( (LA13_2==BY) && ((strict == false))) {s = 42;}
						else if ( (LA13_2==ASC) && ((strict == false))) {s = 43;}
						else if ( (LA13_2==DESC) && ((strict == false))) {s = 44;}
						else if ( (LA13_2==TIMESTAMP) && ((strict == false))) {s = 45;}
						else if ( (LA13_2==TRUE) && ((strict == false))) {s = 46;}
						else if ( (LA13_2==FALSE) && ((strict == false))) {s = 47;}
						else if ( (LA13_2==SCORE) && ((strict == false))) {s = 48;}
						else if ( (LA13_2==ID) && ((strict == false))) {s = 49;}
						 
						input.seek(index13_2);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA13_36 = input.LA(1);
						 
						int index13_36 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_36==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_36);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA13_37 = input.LA(1);
						 
						int index13_37 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_37==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_37);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA13_40 = input.LA(1);
						 
						int index13_40 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_40==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_40);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA13_41 = input.LA(1);
						 
						int index13_41 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_41==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_41);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA13_39 = input.LA(1);
						 
						int index13_39 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_39==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_39);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA13_0 = input.LA(1);
						 
						int index13_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_0==ID) ) {s = 1;}
						else if ( (LA13_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						else if ( (LA13_0==FLOATING_POINT_LITERAL) ) {s = 3;}
						else if ( (LA13_0==DECIMAL_INTEGER_LITERAL) ) {s = 4;}
						else if ( (LA13_0==QUOTED_STRING) ) {s = 5;}
						else if ( (LA13_0==TRUE) ) {s = 6;}
						else if ( (LA13_0==FALSE) ) {s = 7;}
						else if ( (LA13_0==TIMESTAMP) ) {s = 8;}
						else if ( (LA13_0==COLON) && ((strict == false))) {s = 9;}
						 
						input.seek(index13_0);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA13_43 = input.LA(1);
						 
						int index13_43 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_43==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_43);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA13_50 = input.LA(1);
						 
						int index13_50 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_50==DOT) && ((strict == false))) {s = 51;}
						else if ( (LA13_50==RPAREN) && ((strict == false))) {s = 52;}
						else if ( (LA13_50==ID) && ((strict == false))) {s = 53;}
						else if ( (LA13_50==DOUBLE_QUOTE) && ((strict == false))) {s = 54;}
						else if ( (LA13_50==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 55;}
						else if ( (LA13_50==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 56;}
						else if ( (LA13_50==QUOTED_STRING) && ((strict == false))) {s = 57;}
						else if ( (LA13_50==TRUE) && ((strict == false))) {s = 58;}
						else if ( (LA13_50==FALSE) && ((strict == false))) {s = 59;}
						else if ( (LA13_50==TIMESTAMP) && ((strict == false))) {s = 60;}
						else if ( (LA13_50==COLON) && ((strict == false))) {s = 61;}
						 
						input.seek(index13_50);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA13_23 = input.LA(1);
						 
						int index13_23 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_23==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_23);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA13_24 = input.LA(1);
						 
						int index13_24 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_24==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_24);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA13_25 = input.LA(1);
						 
						int index13_25 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_25==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_25);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA13_42 = input.LA(1);
						 
						int index13_42 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_42==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_42);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA13_21 = input.LA(1);
						 
						int index13_21 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_21==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_21);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA13_46 = input.LA(1);
						 
						int index13_46 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_46==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_46);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA13_44 = input.LA(1);
						 
						int index13_44 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_44==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_44);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA13_22 = input.LA(1);
						 
						int index13_22 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_22==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_22);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA13_45 = input.LA(1);
						 
						int index13_45 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_45==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_45);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA13_29 = input.LA(1);
						 
						int index13_29 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_29==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_29);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA13_28 = input.LA(1);
						 
						int index13_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_28==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_28);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA13_30 = input.LA(1);
						 
						int index13_30 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_30==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_30);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA13_47 = input.LA(1);
						 
						int index13_47 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_47==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_47);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA13_26 = input.LA(1);
						 
						int index13_26 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_26==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_26);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA13_27 = input.LA(1);
						 
						int index13_27 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_27==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_27);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA13_34 = input.LA(1);
						 
						int index13_34 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_34==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_34);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA13_48 = input.LA(1);
						 
						int index13_48 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_48==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_48);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA13_35 = input.LA(1);
						 
						int index13_35 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_35==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_35);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA13_31 = input.LA(1);
						 
						int index13_31 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_31==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_31);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA13_33 = input.LA(1);
						 
						int index13_33 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_33==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_33);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA13_49 = input.LA(1);
						 
						int index13_49 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_49==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_49);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA13_32 = input.LA(1);
						 
						int index13_32 = input.index();
						input.rewind();
						s = -1;
						if ( (LA13_32==DOUBLE_QUOTE) && ((strict == false))) {s = 50;}
						 
						input.seek(index13_32);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 13, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA26_eotS =
		"\u00db\uffff";
	static final String DFA26_eofS =
		"\u00db\uffff";
	static final String DFA26_minS =
		"\2\5\104\uffff\1\5\1\uffff\1\13\1\5\30\uffff\1\61\5\13\1\33\2\uffff\1"+
		"\33\17\uffff\35\33\1\13\1\uffff\1\13\1\5\1\13\1\5\1\13\72\33\2\13";
	static final String DFA26_maxS =
		"\2\127\104\uffff\1\127\1\uffff\1\126\1\127\30\uffff\1\110\5\126\1\45\2"+
		"\uffff\1\45\17\uffff\35\33\1\126\1\uffff\1\126\1\127\1\126\1\127\1\126"+
		"\72\33\2\126";
	static final String DFA26_acceptS =
		"\2\uffff\42\2\42\1\1\uffff\1\1\2\uffff\30\1\7\uffff\1\1\1\2\1\uffff\6"+
		"\1\11\2\36\uffff\1\1\101\uffff";
	static final String DFA26_specialS =
		"\1\1\105\uffff\1\7\1\uffff\1\15\32\uffff\1\12\1\13\1\4\1\5\1\3\60\uffff"+
		"\1\2\1\uffff\1\10\1\uffff\1\0\1\uffff\1\14\72\uffff\1\11\1\6}>";
	static final String[] DFA26_transitionS = {
			"\1\17\1\24\1\6\1\32\1\uffff\1\31\1\42\5\uffff\1\25\1\uffff\1\40\1\uffff"+
			"\1\33\5\uffff\1\3\3\uffff\1\36\1\37\1\7\3\uffff\1\2\1\20\1\11\1\26\1"+
			"\27\1\22\1\10\1\12\2\uffff\1\21\1\uffff\1\43\3\uffff\1\1\1\uffff\1\23"+
			"\1\uffff\1\14\1\16\1\30\1\13\13\uffff\1\41\1\uffff\1\4\1\5\11\uffff\1"+
			"\34\1\35\1\15",
			"\1\61\1\67\1\50\1\75\1\uffff\1\74\1\105\5\uffff\1\70\1\uffff\1\103\1"+
			"\uffff\1\76\5\uffff\1\45\3\uffff\1\101\1\102\1\51\3\uffff\1\44\1\63\1"+
			"\53\1\71\1\72\1\65\1\52\1\54\2\uffff\1\64\1\uffff\1\106\3\uffff\1\62"+
			"\1\uffff\1\66\1\uffff\1\56\1\60\1\73\1\55\13\uffff\1\104\1\uffff\1\46"+
			"\1\47\11\uffff\1\77\1\100\1\57",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\125\1\132\1\114\1\140\1\uffff\1\137\1\150\5\uffff\1\133\1\uffff\1"+
			"\146\1\uffff\1\141\5\uffff\1\111\3\uffff\1\144\1\145\1\115\3\uffff\1"+
			"\110\1\126\1\117\1\134\1\135\1\130\1\116\1\120\2\uffff\1\127\1\uffff"+
			"\1\151\3\uffff\1\107\1\uffff\1\131\1\uffff\1\122\1\124\1\136\1\121\13"+
			"\uffff\1\147\1\152\1\112\1\113\11\uffff\1\142\1\143\1\123",
			"",
			"\1\172\7\uffff\1\165\4\uffff\1\153\2\uffff\1\163\1\uffff\1\154\1\uffff"+
			"\1\170\1\164\2\uffff\2\154\1\162\1\157\3\uffff\1\161\2\uffff\2\154\1"+
			"\160\1\uffff\1\155\3\uffff\1\156\1\154\21\uffff\1\166\1\152\13\uffff"+
			"\1\171\1\167",
			"\1\u0085\1\u008b\1\174\1\u0091\1\uffff\1\u0090\6\uffff\1\u008c\3\uffff"+
			"\1\u0092\11\uffff\1\u0095\1\uffff\1\175\3\uffff\1\u0097\1\u0087\1\177"+
			"\1\u008d\1\u008e\1\u0089\1\176\1\u0080\2\uffff\1\u0088\5\uffff\1\u0086"+
			"\1\uffff\1\u008a\1\uffff\1\u0082\1\u0084\1\u008f\1\u0081\15\uffff\1\u0096"+
			"\1\173\11\uffff\1\u0093\1\u0094\1\u0083",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\155\26\uffff\1\u0098",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\2\uffff\1\155\4\uffff\1\u0099"+
			"\21\uffff\1\166\1\152\13\uffff\1\171\1\167",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\2\uffff\1\155\4\uffff\1\u0099"+
			"\21\uffff\1\166\1\152\13\uffff\1\171\1\167",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"\1\u009b\11\uffff\1\u009a",
			"",
			"",
			"\1\u009d\11\uffff\1\u009c",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\u009e",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"\1\u00a9\1\u00af\1\u00a0\1\u00b5\1\uffff\1\u00b4\6\uffff\1\u00b0\3\uffff"+
			"\1\u00b6\11\uffff\1\u00b9\1\uffff\1\u00a1\3\uffff\1\u00bb\1\u00ab\1\u00a3"+
			"\1\u00b1\1\u00b2\1\u00ad\1\u00a2\1\u00a4\2\uffff\1\u00ac\5\uffff\1\u00aa"+
			"\1\uffff\1\u00ae\1\uffff\1\u00a6\1\u00a8\1\u00b3\1\u00a5\15\uffff\1\u00ba"+
			"\1\u009f\11\uffff\1\u00b7\1\u00b8\1\u00a7",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\154\1\uffff\1\170\1\164"+
			"\2\uffff\2\154\1\162\1\157\3\uffff\1\161\2\uffff\2\154\1\160\5\uffff"+
			"\1\156\1\154\21\uffff\1\166\1\152\13\uffff\1\171\1\167",
			"\1\u00c6\1\u00cc\1\u00bd\1\u00d2\1\uffff\1\u00d1\6\uffff\1\u00cd\3\uffff"+
			"\1\u00d3\11\uffff\1\u00d6\1\uffff\1\u00be\3\uffff\1\u00d8\1\u00c8\1\u00c0"+
			"\1\u00ce\1\u00cf\1\u00ca\1\u00bf\1\u00c1\2\uffff\1\u00c9\5\uffff\1\u00c7"+
			"\1\uffff\1\u00cb\1\uffff\1\u00c3\1\u00c5\1\u00d0\1\u00c2\15\uffff\1\u00d7"+
			"\1\u00bc\11\uffff\1\u00d4\1\u00d5\1\u00c4",
			"\1\172\7\uffff\1\165\4\uffff\1\153\2\uffff\1\163\1\uffff\1\154\1\uffff"+
			"\1\170\1\164\2\uffff\2\154\1\162\1\157\3\uffff\1\161\2\uffff\2\154\1"+
			"\160\5\uffff\1\156\1\154\21\uffff\1\166\1\152\13\uffff\1\171\1\167",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00d9",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\u00da",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\u0099\1\uffff\1\170\1\164"+
			"\2\uffff\2\u0099\1\162\7\uffff\2\u0099\7\uffff\1\u0099\21\uffff\1\166"+
			"\1\152\13\uffff\1\171\1\167",
			"\1\172\7\uffff\1\165\7\uffff\1\163\1\uffff\1\154\1\uffff\1\170\1\164"+
			"\2\uffff\2\154\1\162\1\157\3\uffff\1\161\2\uffff\2\154\1\160\5\uffff"+
			"\1\156\1\154\21\uffff\1\166\1\152\13\uffff\1\171\1\167"
	};

	static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
	static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
	static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
	static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
	static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
	static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
	static final short[][] DFA26_transition;

	static {
		int numStates = DFA26_transitionS.length;
		DFA26_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
		}
	}

	protected class DFA26 extends DFA {

		public DFA26(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 26;
			this.eot = DFA26_eot;
			this.eof = DFA26_eof;
			this.min = DFA26_min;
			this.max = DFA26_max;
			this.accept = DFA26_accept;
			this.special = DFA26_special;
			this.transition = DFA26_transition;
		}
		@Override
		public String getDescription() {
			return "553:1: searchNotCondition : ( NOT searchTest -> ^( NEGATION searchTest ) | searchTest -> searchTest );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA26_156 = input.LA(1);
						 
						int index26_156 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_156==EQUALS||(LA26_156 >= GREATERTHAN && LA26_156 <= GREATERTHANOREQUALS)||(LA26_156 >= LESSTHAN && LA26_156 <= LESSTHANOREQUALS)||LA26_156==NOTEQUALS) ) {s = 108;}
						else if ( (LA26_156==NOT) ) {s = 110;}
						else if ( (LA26_156==IN) ) {s = 111;}
						else if ( (LA26_156==LIKE) ) {s = 112;}
						else if ( (LA26_156==IS) ) {s = 113;}
						else if ( (LA26_156==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_156==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_156==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_156==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_156==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_156==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_156==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_156==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_156==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_156==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_156);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA26_0 = input.LA(1);
						 
						int index26_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_0==NOT) ) {s = 1;}
						else if ( (LA26_0==ID) ) {s = 2;}
						else if ( (LA26_0==DOUBLE_QUOTE) && ((strict == false))) {s = 3;}
						else if ( (LA26_0==SCORE) ) {s = 4;}
						else if ( (LA26_0==SELECT) && ((strict == false))) {s = 5;}
						else if ( (LA26_0==AS) && ((strict == false))) {s = 6;}
						else if ( (LA26_0==FROM) && ((strict == false))) {s = 7;}
						else if ( (LA26_0==JOIN) && ((strict == false))) {s = 8;}
						else if ( (LA26_0==INNER) && ((strict == false))) {s = 9;}
						else if ( (LA26_0==LEFT) && ((strict == false))) {s = 10;}
						else if ( (LA26_0==OUTER) && ((strict == false))) {s = 11;}
						else if ( (LA26_0==ON) && ((strict == false))) {s = 12;}
						else if ( (LA26_0==WHERE) && ((strict == false))) {s = 13;}
						else if ( (LA26_0==OR) && ((strict == false))) {s = 14;}
						else if ( (LA26_0==AND) && ((strict == false))) {s = 15;}
						else if ( (LA26_0==IN) && ((strict == false))) {s = 16;}
						else if ( (LA26_0==LIKE) && ((strict == false))) {s = 17;}
						else if ( (LA26_0==IS) && ((strict == false))) {s = 18;}
						else if ( (LA26_0==NULL) && ((strict == false))) {s = 19;}
						else if ( (LA26_0==ANY) ) {s = 20;}
						else if ( (LA26_0==CONTAINS) ) {s = 21;}
						else if ( (LA26_0==IN_FOLDER) ) {s = 22;}
						else if ( (LA26_0==IN_TREE) ) {s = 23;}
						else if ( (LA26_0==ORDER) && ((strict == false))) {s = 24;}
						else if ( (LA26_0==BY) && ((strict == false))) {s = 25;}
						else if ( (LA26_0==ASC) && ((strict == false))) {s = 26;}
						else if ( (LA26_0==DESC) && ((strict == false))) {s = 27;}
						else if ( (LA26_0==TIMESTAMP) ) {s = 28;}
						else if ( (LA26_0==TRUE) ) {s = 29;}
						else if ( (LA26_0==FALSE) ) {s = 30;}
						else if ( (LA26_0==FLOATING_POINT_LITERAL) ) {s = 31;}
						else if ( (LA26_0==DECIMAL_INTEGER_LITERAL) ) {s = 32;}
						else if ( (LA26_0==QUOTED_STRING) ) {s = 33;}
						else if ( (LA26_0==COLON) && ((strict == false))) {s = 34;}
						else if ( (LA26_0==LPAREN) ) {s = 35;}
						 
						input.seek(index26_0);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA26_152 = input.LA(1);
						 
						int index26_152 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_152==EQUALS||(LA26_152 >= GREATERTHAN && LA26_152 <= GREATERTHANOREQUALS)||(LA26_152 >= LESSTHAN && LA26_152 <= LESSTHANOREQUALS)||LA26_152==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_152==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_152==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_152==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_152==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_152==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_152==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_152==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_152==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_152==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_152==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_152);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA26_103 = input.LA(1);
						 
						int index26_103 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_103==EQUALS||(LA26_103 >= GREATERTHAN && LA26_103 <= GREATERTHANOREQUALS)||(LA26_103 >= LESSTHAN && LA26_103 <= LESSTHANOREQUALS)||LA26_103==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_103==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_103==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_103==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_103==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_103==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_103==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_103==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_103==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_103==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_103==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_103);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA26_101 = input.LA(1);
						 
						int index26_101 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_101==EQUALS||(LA26_101 >= GREATERTHAN && LA26_101 <= GREATERTHANOREQUALS)||(LA26_101 >= LESSTHAN && LA26_101 <= LESSTHANOREQUALS)||LA26_101==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_101==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_101==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_101==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_101==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_101==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_101==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_101==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_101==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_101==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_101==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_101);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA26_102 = input.LA(1);
						 
						int index26_102 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_102==EQUALS||(LA26_102 >= GREATERTHAN && LA26_102 <= GREATERTHANOREQUALS)||(LA26_102 >= LESSTHAN && LA26_102 <= LESSTHANOREQUALS)||LA26_102==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_102==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_102==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_102==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_102==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_102==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_102==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_102==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_102==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_102==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_102==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_102);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA26_218 = input.LA(1);
						 
						int index26_218 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_218==EQUALS||(LA26_218 >= GREATERTHAN && LA26_218 <= GREATERTHANOREQUALS)||(LA26_218 >= LESSTHAN && LA26_218 <= LESSTHANOREQUALS)||LA26_218==NOTEQUALS) ) {s = 108;}
						else if ( (LA26_218==NOT) ) {s = 110;}
						else if ( (LA26_218==IN) ) {s = 111;}
						else if ( (LA26_218==LIKE) ) {s = 112;}
						else if ( (LA26_218==IS) ) {s = 113;}
						else if ( (LA26_218==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_218==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_218==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_218==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_218==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_218==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_218==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_218==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_218==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_218==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_218);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA26_70 = input.LA(1);
						 
						int index26_70 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_70==NOT) ) {s = 71;}
						else if ( (LA26_70==ID) ) {s = 72;}
						else if ( (LA26_70==DOUBLE_QUOTE) ) {s = 73;}
						else if ( (LA26_70==SCORE) ) {s = 74;}
						else if ( (LA26_70==SELECT) ) {s = 75;}
						else if ( (LA26_70==AS) ) {s = 76;}
						else if ( (LA26_70==FROM) ) {s = 77;}
						else if ( (LA26_70==JOIN) ) {s = 78;}
						else if ( (LA26_70==INNER) ) {s = 79;}
						else if ( (LA26_70==LEFT) ) {s = 80;}
						else if ( (LA26_70==OUTER) ) {s = 81;}
						else if ( (LA26_70==ON) ) {s = 82;}
						else if ( (LA26_70==WHERE) ) {s = 83;}
						else if ( (LA26_70==OR) ) {s = 84;}
						else if ( (LA26_70==AND) ) {s = 85;}
						else if ( (LA26_70==IN) ) {s = 86;}
						else if ( (LA26_70==LIKE) ) {s = 87;}
						else if ( (LA26_70==IS) ) {s = 88;}
						else if ( (LA26_70==NULL) ) {s = 89;}
						else if ( (LA26_70==ANY) ) {s = 90;}
						else if ( (LA26_70==CONTAINS) ) {s = 91;}
						else if ( (LA26_70==IN_FOLDER) ) {s = 92;}
						else if ( (LA26_70==IN_TREE) ) {s = 93;}
						else if ( (LA26_70==ORDER) ) {s = 94;}
						else if ( (LA26_70==BY) ) {s = 95;}
						else if ( (LA26_70==ASC) ) {s = 96;}
						else if ( (LA26_70==DESC) ) {s = 97;}
						else if ( (LA26_70==TIMESTAMP) ) {s = 98;}
						else if ( (LA26_70==TRUE) ) {s = 99;}
						else if ( (LA26_70==FALSE) ) {s = 100;}
						else if ( (LA26_70==FLOATING_POINT_LITERAL) ) {s = 101;}
						else if ( (LA26_70==DECIMAL_INTEGER_LITERAL) ) {s = 102;}
						else if ( (LA26_70==QUOTED_STRING) ) {s = 103;}
						else if ( (LA26_70==COLON) ) {s = 104;}
						else if ( (LA26_70==LPAREN) ) {s = 105;}
						else if ( (LA26_70==RPAREN) && ((strict == false))) {s = 106;}
						 
						input.seek(index26_70);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA26_154 = input.LA(1);
						 
						int index26_154 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_154==EQUALS||(LA26_154 >= GREATERTHAN && LA26_154 <= GREATERTHANOREQUALS)||(LA26_154 >= LESSTHAN && LA26_154 <= LESSTHANOREQUALS)||LA26_154==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_154==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_154==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_154==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_154==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_154==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_154==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_154==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_154==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_154==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_154==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_154);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA26_217 = input.LA(1);
						 
						int index26_217 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_217==EQUALS||(LA26_217 >= GREATERTHAN && LA26_217 <= GREATERTHANOREQUALS)||(LA26_217 >= LESSTHAN && LA26_217 <= LESSTHANOREQUALS)||LA26_217==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_217==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_217==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_217==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_217==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_217==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_217==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_217==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_217==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_217==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_217==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_217);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA26_99 = input.LA(1);
						 
						int index26_99 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_99==LPAREN) ) {s = 109;}
						else if ( (LA26_99==EQUALS||(LA26_99 >= GREATERTHAN && LA26_99 <= GREATERTHANOREQUALS)||(LA26_99 >= LESSTHAN && LA26_99 <= LESSTHANOREQUALS)||LA26_99==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_99==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_99==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_99==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_99==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_99==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_99==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_99==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_99==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_99==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_99==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_99);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA26_100 = input.LA(1);
						 
						int index26_100 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_100==LPAREN) ) {s = 109;}
						else if ( (LA26_100==EQUALS||(LA26_100 >= GREATERTHAN && LA26_100 <= GREATERTHANOREQUALS)||(LA26_100 >= LESSTHAN && LA26_100 <= LESSTHANOREQUALS)||LA26_100==NOTEQUALS) ) {s = 153;}
						else if ( (LA26_100==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_100==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_100==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_100==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_100==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_100==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_100==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_100==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_100==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_100==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_100);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA26_158 = input.LA(1);
						 
						int index26_158 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_158==DOT) ) {s = 107;}
						else if ( (LA26_158==EQUALS||(LA26_158 >= GREATERTHAN && LA26_158 <= GREATERTHANOREQUALS)||(LA26_158 >= LESSTHAN && LA26_158 <= LESSTHANOREQUALS)||LA26_158==NOTEQUALS) ) {s = 108;}
						else if ( (LA26_158==NOT) ) {s = 110;}
						else if ( (LA26_158==IN) ) {s = 111;}
						else if ( (LA26_158==LIKE) ) {s = 112;}
						else if ( (LA26_158==IS) ) {s = 113;}
						else if ( (LA26_158==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_158==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_158==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_158==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_158==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_158==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_158==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_158==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_158==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_158==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_158);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA26_72 = input.LA(1);
						 
						int index26_72 = input.index();
						input.rewind();
						s = -1;
						if ( (LA26_72==DOT) ) {s = 107;}
						else if ( (LA26_72==EQUALS||(LA26_72 >= GREATERTHAN && LA26_72 <= GREATERTHANOREQUALS)||(LA26_72 >= LESSTHAN && LA26_72 <= LESSTHANOREQUALS)||LA26_72==NOTEQUALS) ) {s = 108;}
						else if ( (LA26_72==LPAREN) ) {s = 109;}
						else if ( (LA26_72==NOT) ) {s = 110;}
						else if ( (LA26_72==IN) ) {s = 111;}
						else if ( (LA26_72==LIKE) ) {s = 112;}
						else if ( (LA26_72==IS) ) {s = 113;}
						else if ( (LA26_72==RPAREN) && ((strict == false))) {s = 106;}
						else if ( (LA26_72==ID) && ((strict == false))) {s = 114;}
						else if ( (LA26_72==DOUBLE_QUOTE) && ((strict == false))) {s = 115;}
						else if ( (LA26_72==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 116;}
						else if ( (LA26_72==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 117;}
						else if ( (LA26_72==QUOTED_STRING) && ((strict == false))) {s = 118;}
						else if ( (LA26_72==TRUE) && ((strict == false))) {s = 119;}
						else if ( (LA26_72==FALSE) && ((strict == false))) {s = 120;}
						else if ( (LA26_72==TIMESTAMP) && ((strict == false))) {s = 121;}
						else if ( (LA26_72==COLON) && ((strict == false))) {s = 122;}
						 
						input.seek(index26_72);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 26, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA28_eotS =
		"\u0113\uffff";
	static final String DFA28_eofS =
		"\u00a6\uffff\1\u00ec\36\uffff\1\u00f2\36\uffff\1\u00f2\56\uffff";
	static final String DFA28_minS =
		"\1\5\1\30\1\5\21\uffff\1\33\3\61\4\uffff\1\61\2\35\4\uffff\1\33\2\uffff"+
		"\1\46\3\uffff\35\33\2\uffff\3\13\2\uffff\1\35\1\5\1\30\1\13\1\5\1\13\7"+
		"\uffff\1\13\1\5\2\13\1\5\1\13\36\33\1\uffff\1\46\10\uffff\35\33\1\5\1"+
		"\uffff\35\33\1\5\1\uffff\35\33\1\5\2\35\1\5\1\13\5\uffff\1\13\5\uffff"+
		"\1\13\35\33\1\35";
	static final String DFA28_maxS =
		"\1\127\1\66\1\127\21\uffff\4\61\4\uffff\1\110\2\66\4\uffff\1\45\2\uffff"+
		"\1\57\3\uffff\35\33\2\uffff\3\126\2\uffff\1\66\1\127\1\66\1\126\1\127"+
		"\1\126\7\uffff\1\126\1\127\2\126\1\127\1\126\35\33\1\45\1\uffff\1\57\10"+
		"\uffff\35\33\1\111\1\uffff\35\33\1\111\1\uffff\35\33\1\111\2\66\1\127"+
		"\1\126\5\uffff\1\126\5\uffff\1\126\35\33\1\66";
	static final String DFA28_acceptS =
		"\3\uffff\21\1\4\uffff\4\1\3\uffff\4\5\1\uffff\2\1\1\uffff\1\2\1\3\1\4"+
		"\35\uffff\2\6\3\uffff\2\5\6\uffff\7\1\44\uffff\1\1\1\uffff\1\2\1\3\1\4"+
		"\1\7\4\1\36\uffff\1\10\36\uffff\1\10\42\uffff\5\7\1\uffff\5\10\37\uffff";
	static final String DFA28_specialS =
		"\1\42\1\102\1\72\21\uffff\1\2\7\uffff\1\51\1\12\1\14\13\uffff\1\104\1"+
		"\112\1\117\1\124\1\106\1\113\1\73\1\75\1\50\1\56\1\34\1\43\1\20\1\25\1"+
		"\10\1\5\1\13\1\0\1\4\1\125\1\121\1\114\1\105\1\76\1\70\1\57\1\47\1\67"+
		"\1\115\2\uffff\1\120\1\40\1\37\4\uffff\1\123\1\1\1\uffff\1\64\7\uffff"+
		"\1\27\1\uffff\1\6\1\26\1\uffff\1\7\35\uffff\1\60\1\uffff\1\62\45\uffff"+
		"\1\122\36\uffff\1\22\36\uffff\1\32\1\uffff\1\53\1\3\1\31\5\uffff\1\24"+
		"\5\uffff\1\23\1\21\1\17\1\16\1\15\1\44\1\41\1\36\1\35\1\33\1\30\1\55\1"+
		"\61\1\63\1\45\1\46\1\52\1\54\1\77\1\100\1\101\1\66\1\71\1\74\1\110\1\107"+
		"\1\111\1\103\1\116\1\11\1\65}>";
	static final String[] DFA28_transitionS = {
			"\1\16\1\24\1\5\1\32\1\uffff\1\31\1\42\5\uffff\1\25\1\uffff\1\40\1\uffff"+
			"\1\33\5\uffff\1\2\3\uffff\1\36\1\37\1\6\3\uffff\1\1\1\20\1\10\1\26\1"+
			"\27\1\22\1\7\1\11\2\uffff\1\21\5\uffff\1\17\1\uffff\1\23\1\uffff\1\13"+
			"\1\15\1\30\1\12\13\uffff\1\41\1\uffff\1\3\1\4\11\uffff\1\34\1\35\1\14",
			"\1\43\4\uffff\1\44\5\uffff\2\44\1\uffff\1\47\3\uffff\1\51\2\uffff\2"+
			"\44\1\50\1\uffff\1\45\3\uffff\1\46\1\44",
			"\1\64\1\72\1\53\1\100\1\uffff\1\77\6\uffff\1\73\3\uffff\1\101\11\uffff"+
			"\1\104\1\uffff\1\54\3\uffff\1\106\1\66\1\56\1\74\1\75\1\70\1\55\1\57"+
			"\2\uffff\1\67\5\uffff\1\65\1\uffff\1\71\1\uffff\1\61\1\63\1\76\1\60\15"+
			"\uffff\1\105\1\52\11\uffff\1\102\1\103\1\62",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\110\11\uffff\1\107\13\uffff\1\45",
			"\1\111",
			"\1\112",
			"\1\113",
			"",
			"",
			"",
			"",
			"\1\45\26\uffff\1\114",
			"\1\115\5\uffff\2\115\10\uffff\2\115\2\uffff\1\45\4\uffff\1\115",
			"\1\115\5\uffff\2\115\10\uffff\2\115\2\uffff\1\45\4\uffff\1\115",
			"",
			"",
			"",
			"",
			"\1\117\11\uffff\1\116",
			"",
			"",
			"\1\47\10\uffff\1\50",
			"",
			"",
			"",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"\1\120",
			"",
			"",
			"\1\131\7\uffff\1\125\7\uffff\1\122\3\uffff\1\127\1\124\4\uffff\1\121"+
			"\42\uffff\1\123\1\132\13\uffff\1\130\1\126",
			"\1\131\7\uffff\1\125\7\uffff\1\134\3\uffff\1\127\1\124\4\uffff\1\133"+
			"\42\uffff\1\135\1\132\13\uffff\1\130\1\126",
			"\1\131\7\uffff\1\125\7\uffff\1\137\3\uffff\1\127\1\124\4\uffff\1\136"+
			"\42\uffff\1\140\1\132\13\uffff\1\130\1\126",
			"",
			"",
			"\1\44\5\uffff\2\44\1\uffff\1\47\3\uffff\1\51\2\uffff\2\44\1\50\5\uffff"+
			"\1\46\1\44",
			"\1\153\1\161\1\142\1\167\1\uffff\1\166\6\uffff\1\162\3\uffff\1\170\11"+
			"\uffff\1\173\1\uffff\1\143\3\uffff\1\175\1\155\1\145\1\163\1\164\1\157"+
			"\1\144\1\146\2\uffff\1\156\5\uffff\1\154\1\uffff\1\160\1\uffff\1\150"+
			"\1\152\1\165\1\147\15\uffff\1\174\1\141\11\uffff\1\171\1\172\1\151",
			"\1\176\4\uffff\1\177\5\uffff\2\177\1\uffff\1\u0081\3\uffff\1\u0083\2"+
			"\uffff\2\177\1\u0082\5\uffff\1\u0080\1\177",
			"\1\131\3\uffff\1\u0084\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"\1\u0093\1\u0099\1\u008a\1\u009f\1\uffff\1\u009e\6\uffff\1\u009a\3\uffff"+
			"\1\u00a0\11\uffff\1\u00a3\1\uffff\1\u008b\3\uffff\1\u00a5\1\u0095\1\u008d"+
			"\1\u009b\1\u009c\1\u0097\1\u008c\1\u008e\2\uffff\1\u0096\5\uffff\1\u0094"+
			"\1\uffff\1\u0098\1\uffff\1\u0090\1\u0092\1\u009d\1\u008f\15\uffff\1\u00a4"+
			"\1\u0089\11\uffff\1\u00a1\1\u00a2\1\u0091",
			"\1\131\7\uffff\1\125\7\uffff\1\u0087\3\uffff\1\127\1\124\4\uffff\1\u0086"+
			"\42\uffff\1\u0088\1\u00a6\13\uffff\1\130\1\126",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\131\3\uffff\1\u00a7\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"\1\u00b2\1\u00b8\1\u00a9\1\u00be\1\uffff\1\u00bd\6\uffff\1\u00b9\3\uffff"+
			"\1\u00bf\11\uffff\1\u00c2\1\uffff\1\u00aa\3\uffff\1\u00c4\1\u00b4\1\u00ac"+
			"\1\u00ba\1\u00bb\1\u00b6\1\u00ab\1\u00ad\2\uffff\1\u00b5\5\uffff\1\u00b3"+
			"\1\uffff\1\u00b7\1\uffff\1\u00af\1\u00b1\1\u00bc\1\u00ae\15\uffff\1\u00c3"+
			"\1\u00a8\11\uffff\1\u00c0\1\u00c1\1\u00b0",
			"\1\131\7\uffff\1\125\7\uffff\1\u0087\3\uffff\1\127\1\124\4\uffff\1\u0086"+
			"\42\uffff\1\u0088\1\u00c5\13\uffff\1\130\1\126",
			"\1\131\3\uffff\1\u00c6\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"\1\u00d1\1\u00d7\1\u00c8\1\u00dd\1\uffff\1\u00dc\6\uffff\1\u00d8\3\uffff"+
			"\1\u00de\11\uffff\1\u00e1\1\uffff\1\u00c9\3\uffff\1\u00e3\1\u00d3\1\u00cb"+
			"\1\u00d9\1\u00da\1\u00d5\1\u00ca\1\u00cc\2\uffff\1\u00d4\5\uffff\1\u00d2"+
			"\1\uffff\1\u00d6\1\uffff\1\u00ce\1\u00d0\1\u00db\1\u00cd\15\uffff\1\u00e2"+
			"\1\u00c7\11\uffff\1\u00df\1\u00e0\1\u00cf",
			"\1\131\7\uffff\1\125\7\uffff\1\u0087\3\uffff\1\127\1\124\4\uffff\1\u0086"+
			"\42\uffff\1\u0088\1\u00e4\13\uffff\1\130\1\126",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e5",
			"\1\u00e7\11\uffff\1\u00e6",
			"",
			"\1\u0081\10\uffff\1\u0082",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e8",
			"\1\u00e9\27\uffff\1\177\5\uffff\2\177\10\uffff\2\177\7\uffff\1\177\3"+
			"\uffff\1\u00ea\1\u00eb\15\uffff\1\u00ed",
			"",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ee",
			"\1\u00ef\27\uffff\1\177\5\uffff\2\177\10\uffff\2\177\7\uffff\1\177\3"+
			"\uffff\1\u00f0\1\u00f1\15\uffff\1\u00f3",
			"",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00f4",
			"\1\u00ef\27\uffff\1\177\5\uffff\2\177\10\uffff\2\177\7\uffff\1\177\3"+
			"\uffff\1\u00f0\1\u00f1\15\uffff\1\u00f3",
			"\1\44\5\uffff\2\44\1\uffff\1\47\3\uffff\1\51\2\uffff\2\44\1\50\5\uffff"+
			"\1\46\1\44",
			"\1\177\5\uffff\2\177\1\uffff\1\u0081\3\uffff\1\u0083\2\uffff\2\177\1"+
			"\u0082\5\uffff\1\u0080\1\177",
			"\1\u00ff\1\u0105\1\u00f6\1\u010b\1\uffff\1\u010a\6\uffff\1\u0106\3\uffff"+
			"\1\u010c\11\uffff\1\u010f\1\uffff\1\u00f7\3\uffff\1\u0111\1\u0101\1\u00f9"+
			"\1\u0107\1\u0108\1\u0103\1\u00f8\1\u00fa\2\uffff\1\u0102\5\uffff\1\u0100"+
			"\1\uffff\1\u0104\1\uffff\1\u00fc\1\u00fe\1\u0109\1\u00fb\15\uffff\1\u0110"+
			"\1\u00f5\11\uffff\1\u010d\1\u010e\1\u00fd",
			"\1\131\3\uffff\1\u0084\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"",
			"",
			"",
			"",
			"",
			"\1\131\3\uffff\1\u00a7\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"",
			"",
			"",
			"",
			"",
			"\1\131\3\uffff\1\u00c6\3\uffff\1\125\4\uffff\1\u0085\2\uffff\1\u0087"+
			"\3\uffff\1\127\1\124\4\uffff\1\u0086\42\uffff\1\u0088\1\132\13\uffff"+
			"\1\130\1\126",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\u0112",
			"\1\177\5\uffff\2\177\1\uffff\1\u0081\3\uffff\1\u0083\2\uffff\2\177\1"+
			"\u0082\5\uffff\1\u0080\1\177"
	};

	static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
	static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
	static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
	static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
	static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
	static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
	static final short[][] DFA28_transition;

	static {
		int numStates = DFA28_transitionS.length;
		DFA28_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
		}
	}

	protected class DFA28 extends DFA {

		public DFA28(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 28;
			this.eot = DFA28_eot;
			this.eof = DFA28_eof;
			this.min = DFA28_min;
			this.max = DFA28_max;
			this.accept = DFA28_accept;
			this.special = DFA28_special;
			this.transition = DFA28_transition;
		}
		@Override
		public String getDescription() {
			return "570:1: predicate : ( comparisonPredicate | inPredicate | likePredicate | nullPredicate | quantifiedComparisonPredicate | quantifiedInPredicate | textSearchPredicate | folderPredicate );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA28_59 = input.LA(1);
						 
						int index28_59 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_59==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_59);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA28_81 = input.LA(1);
						 
						int index28_81 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_81==COMMA) ) {s = 132;}
						else if ( (LA28_81==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_81==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_81==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_81==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_81==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_81==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_81==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_81==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_81==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_81==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_81==COLON) && ((strict == false))) {s = 89;}
						 
						input.seek(index28_81);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA28_20 = input.LA(1);
						 
						int index28_20 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_20==LPAREN) && ((strict == false))) {s = 37;}
						else if ( (LA28_20==ID) ) {s = 71;}
						else if ( (LA28_20==DOUBLE_QUOTE) ) {s = 72;}
						 
						input.seek(index28_20);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA28_231 = input.LA(1);
						 
						int index28_231 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_231==SELECT) && ((strict == false))) {s = 245;}
						else if ( (LA28_231==AS) && ((strict == false))) {s = 246;}
						else if ( (LA28_231==FROM) && ((strict == false))) {s = 247;}
						else if ( (LA28_231==JOIN) && ((strict == false))) {s = 248;}
						else if ( (LA28_231==INNER) && ((strict == false))) {s = 249;}
						else if ( (LA28_231==LEFT) && ((strict == false))) {s = 250;}
						else if ( (LA28_231==OUTER) && ((strict == false))) {s = 251;}
						else if ( (LA28_231==ON) && ((strict == false))) {s = 252;}
						else if ( (LA28_231==WHERE) && ((strict == false))) {s = 253;}
						else if ( (LA28_231==OR) && ((strict == false))) {s = 254;}
						else if ( (LA28_231==AND) && ((strict == false))) {s = 255;}
						else if ( (LA28_231==NOT) && ((strict == false))) {s = 256;}
						else if ( (LA28_231==IN) && ((strict == false))) {s = 257;}
						else if ( (LA28_231==LIKE) && ((strict == false))) {s = 258;}
						else if ( (LA28_231==IS) && ((strict == false))) {s = 259;}
						else if ( (LA28_231==NULL) && ((strict == false))) {s = 260;}
						else if ( (LA28_231==ANY) && ((strict == false))) {s = 261;}
						else if ( (LA28_231==CONTAINS) && ((strict == false))) {s = 262;}
						else if ( (LA28_231==IN_FOLDER) && ((strict == false))) {s = 263;}
						else if ( (LA28_231==IN_TREE) && ((strict == false))) {s = 264;}
						else if ( (LA28_231==ORDER) && ((strict == false))) {s = 265;}
						else if ( (LA28_231==BY) && ((strict == false))) {s = 266;}
						else if ( (LA28_231==ASC) && ((strict == false))) {s = 267;}
						else if ( (LA28_231==DESC) && ((strict == false))) {s = 268;}
						else if ( (LA28_231==TIMESTAMP) && ((strict == false))) {s = 269;}
						else if ( (LA28_231==TRUE) && ((strict == false))) {s = 270;}
						else if ( (LA28_231==FALSE) && ((strict == false))) {s = 271;}
						else if ( (LA28_231==SCORE) && ((strict == false))) {s = 272;}
						else if ( (LA28_231==ID) && ((strict == false))) {s = 273;}
						 
						input.seek(index28_231);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA28_60 = input.LA(1);
						 
						int index28_60 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_60==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_60);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA28_57 = input.LA(1);
						 
						int index28_57 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_57==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_57);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA28_93 = input.LA(1);
						 
						int index28_93 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_93==RPAREN) ) {s = 197;}
						else if ( (LA28_93==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_93==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_93==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_93==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_93==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_93==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_93==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_93==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_93==COLON) && ((strict == false))) {s = 89;}
						 
						input.seek(index28_93);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA28_96 = input.LA(1);
						 
						int index28_96 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_96==RPAREN) ) {s = 228;}
						else if ( (LA28_96==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_96==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_96==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_96==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_96==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_96==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_96==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_96==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_96==COLON) && ((strict == false))) {s = 89;}
						 
						input.seek(index28_96);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA28_56 = input.LA(1);
						 
						int index28_56 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_56==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_56);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA28_273 = input.LA(1);
						 
						int index28_273 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_273==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_273);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA28_29 = input.LA(1);
						 
						int index28_29 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_29==LPAREN) && ((strict == false))) {s = 37;}
						else if ( (LA28_29==EQUALS||(LA28_29 >= GREATERTHAN && LA28_29 <= GREATERTHANOREQUALS)||(LA28_29 >= LESSTHAN && LA28_29 <= LESSTHANOREQUALS)||LA28_29==NOTEQUALS) ) {s = 77;}
						 
						input.seek(index28_29);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA28_58 = input.LA(1);
						 
						int index28_58 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_58==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_58);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA28_30 = input.LA(1);
						 
						int index28_30 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_30==LPAREN) && ((strict == false))) {s = 37;}
						else if ( (LA28_30==EQUALS||(LA28_30 >= GREATERTHAN && LA28_30 <= GREATERTHANOREQUALS)||(LA28_30 >= LESSTHAN && LA28_30 <= LESSTHANOREQUALS)||LA28_30==NOTEQUALS) ) {s = 77;}
						 
						input.seek(index28_30);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA28_248 = input.LA(1);
						 
						int index28_248 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_248==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_248);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA28_247 = input.LA(1);
						 
						int index28_247 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_247==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_247);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA28_246 = input.LA(1);
						 
						int index28_246 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_246==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_246);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA28_54 = input.LA(1);
						 
						int index28_54 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_54==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_54);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA28_245 = input.LA(1);
						 
						int index28_245 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_245==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_245);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA28_197 = input.LA(1);
						 
						int index28_197 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_197==EQUALS||(LA28_197 >= GREATERTHAN && LA28_197 <= GREATERTHANOREQUALS)||(LA28_197 >= LESSTHAN && LA28_197 <= LESSTHANOREQUALS)||LA28_197==NOTEQUALS) && ((strict == false))) {s = 127;}
						else if ( (LA28_197==AND) ) {s = 239;}
						else if ( (LA28_197==OR) ) {s = 240;}
						else if ( (LA28_197==ORDER) ) {s = 241;}
						else if ( (LA28_197==EOF) ) {s = 242;}
						else if ( (LA28_197==RPAREN) ) {s = 243;}
						 
						input.seek(index28_197);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA28_244 = input.LA(1);
						 
						int index28_244 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_244==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_244==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_244==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_244==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_244==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_244==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_244==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_244==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_244==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_244==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_244==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_244==COMMA) ) {s = 198;}
						 
						input.seek(index28_244);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA28_238 = input.LA(1);
						 
						int index28_238 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_238==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_238==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_238==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_238==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_238==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_238==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_238==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_238==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_238==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_238==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_238==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_238==COMMA) ) {s = 167;}
						 
						input.seek(index28_238);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA28_55 = input.LA(1);
						 
						int index28_55 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_55==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_55);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA28_94 = input.LA(1);
						 
						int index28_94 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_94==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_94==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_94==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_94==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_94==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_94==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_94==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_94==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_94==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_94==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_94==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_94==COMMA) ) {s = 198;}
						 
						input.seek(index28_94);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA28_91 = input.LA(1);
						 
						int index28_91 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_91==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_91==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_91==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_91==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_91==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_91==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_91==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_91==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_91==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_91==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_91==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_91==COMMA) ) {s = 167;}
						 
						input.seek(index28_91);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA28_254 = input.LA(1);
						 
						int index28_254 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_254==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_254);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA28_232 = input.LA(1);
						 
						int index28_232 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_232==COMMA) ) {s = 132;}
						else if ( (LA28_232==DOT) && ((strict == false))) {s = 133;}
						else if ( (LA28_232==RPAREN) && ((strict == false))) {s = 90;}
						else if ( (LA28_232==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_232==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_232==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_232==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_232==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_232==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_232==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_232==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_232==COLON) && ((strict == false))) {s = 89;}
						 
						input.seek(index28_232);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA28_228 = input.LA(1);
						 
						int index28_228 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_228==EQUALS||(LA28_228 >= GREATERTHAN && LA28_228 <= GREATERTHANOREQUALS)||(LA28_228 >= LESSTHAN && LA28_228 <= LESSTHANOREQUALS)||LA28_228==NOTEQUALS) && ((strict == false))) {s = 127;}
						else if ( (LA28_228==AND) ) {s = 239;}
						else if ( (LA28_228==OR) ) {s = 240;}
						else if ( (LA28_228==ORDER) ) {s = 241;}
						else if ( (LA28_228==EOF) ) {s = 242;}
						else if ( (LA28_228==RPAREN) ) {s = 243;}
						 
						input.seek(index28_228);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA28_253 = input.LA(1);
						 
						int index28_253 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_253==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_253);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA28_52 = input.LA(1);
						 
						int index28_52 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_52==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_52);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA28_252 = input.LA(1);
						 
						int index28_252 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_252==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_252);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA28_251 = input.LA(1);
						 
						int index28_251 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_251==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_251);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA28_75 = input.LA(1);
						 
						int index28_75 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_75==ID) ) {s = 94;}
						else if ( (LA28_75==DOUBLE_QUOTE) ) {s = 95;}
						else if ( (LA28_75==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_75==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_75==QUOTED_STRING) ) {s = 96;}
						else if ( (LA28_75==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_75==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_75==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_75==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_75==RPAREN) && ((strict == false))) {s = 90;}
						 
						input.seek(index28_75);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA28_74 = input.LA(1);
						 
						int index28_74 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_74==ID) ) {s = 91;}
						else if ( (LA28_74==DOUBLE_QUOTE) ) {s = 92;}
						else if ( (LA28_74==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_74==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_74==QUOTED_STRING) ) {s = 93;}
						else if ( (LA28_74==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_74==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_74==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_74==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_74==RPAREN) && ((strict == false))) {s = 90;}
						 
						input.seek(index28_74);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA28_250 = input.LA(1);
						 
						int index28_250 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_250==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_250);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA28_0 = input.LA(1);
						 
						int index28_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_0==ID) ) {s = 1;}
						else if ( (LA28_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						else if ( (LA28_0==SCORE) ) {s = 3;}
						else if ( (LA28_0==SELECT) && ((strict == false))) {s = 4;}
						else if ( (LA28_0==AS) && ((strict == false))) {s = 5;}
						else if ( (LA28_0==FROM) && ((strict == false))) {s = 6;}
						else if ( (LA28_0==JOIN) && ((strict == false))) {s = 7;}
						else if ( (LA28_0==INNER) && ((strict == false))) {s = 8;}
						else if ( (LA28_0==LEFT) && ((strict == false))) {s = 9;}
						else if ( (LA28_0==OUTER) && ((strict == false))) {s = 10;}
						else if ( (LA28_0==ON) && ((strict == false))) {s = 11;}
						else if ( (LA28_0==WHERE) && ((strict == false))) {s = 12;}
						else if ( (LA28_0==OR) && ((strict == false))) {s = 13;}
						else if ( (LA28_0==AND) && ((strict == false))) {s = 14;}
						else if ( (LA28_0==NOT) && ((strict == false))) {s = 15;}
						else if ( (LA28_0==IN) && ((strict == false))) {s = 16;}
						else if ( (LA28_0==LIKE) && ((strict == false))) {s = 17;}
						else if ( (LA28_0==IS) && ((strict == false))) {s = 18;}
						else if ( (LA28_0==NULL) && ((strict == false))) {s = 19;}
						else if ( (LA28_0==ANY) ) {s = 20;}
						else if ( (LA28_0==CONTAINS) ) {s = 21;}
						else if ( (LA28_0==IN_FOLDER) ) {s = 22;}
						else if ( (LA28_0==IN_TREE) ) {s = 23;}
						else if ( (LA28_0==ORDER) && ((strict == false))) {s = 24;}
						else if ( (LA28_0==BY) && ((strict == false))) {s = 25;}
						else if ( (LA28_0==ASC) && ((strict == false))) {s = 26;}
						else if ( (LA28_0==DESC) && ((strict == false))) {s = 27;}
						else if ( (LA28_0==TIMESTAMP) ) {s = 28;}
						else if ( (LA28_0==TRUE) ) {s = 29;}
						else if ( (LA28_0==FALSE) ) {s = 30;}
						else if ( (LA28_0==FLOATING_POINT_LITERAL) ) {s = 31;}
						else if ( (LA28_0==DECIMAL_INTEGER_LITERAL) ) {s = 32;}
						else if ( (LA28_0==QUOTED_STRING) ) {s = 33;}
						else if ( (LA28_0==COLON) && ((strict == false))) {s = 34;}
						 
						input.seek(index28_0);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA28_53 = input.LA(1);
						 
						int index28_53 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_53==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_53);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA28_249 = input.LA(1);
						 
						int index28_249 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_249==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_249);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA28_258 = input.LA(1);
						 
						int index28_258 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_258==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_258);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA28_259 = input.LA(1);
						 
						int index28_259 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_259==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_259);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA28_68 = input.LA(1);
						 
						int index28_68 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_68==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_68);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA28_50 = input.LA(1);
						 
						int index28_50 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_50==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_50);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA28_28 = input.LA(1);
						 
						int index28_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_28==QUOTED_STRING) ) {s = 76;}
						else if ( (LA28_28==LPAREN) && ((strict == false))) {s = 37;}
						 
						input.seek(index28_28);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA28_260 = input.LA(1);
						 
						int index28_260 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_260==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_260);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA28_230 = input.LA(1);
						 
						int index28_230 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_230==EQUALS||(LA28_230 >= GREATERTHAN && LA28_230 <= GREATERTHANOREQUALS)||(LA28_230 >= LESSTHAN && LA28_230 <= LESSTHANOREQUALS)||LA28_230==NOTEQUALS) && ((strict == false))) {s = 127;}
						else if ( (LA28_230==NOT) && ((strict == false))) {s = 128;}
						else if ( (LA28_230==IN) && ((strict == false))) {s = 129;}
						else if ( (LA28_230==LIKE) && ((strict == false))) {s = 130;}
						else if ( (LA28_230==IS) && ((strict == false))) {s = 131;}
						 
						input.seek(index28_230);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA28_261 = input.LA(1);
						 
						int index28_261 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_261==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_261);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA28_255 = input.LA(1);
						 
						int index28_255 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_255==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_255);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA28_51 = input.LA(1);
						 
						int index28_51 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_51==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_51);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA28_67 = input.LA(1);
						 
						int index28_67 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_67==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_67);
						if ( s>=0 ) return s;
						break;

					case 48 : 
						int LA28_126 = input.LA(1);
						 
						int index28_126 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_126==ID) && ((strict == false))) {s = 230;}
						else if ( (LA28_126==DOUBLE_QUOTE) && ((strict == false))) {s = 231;}
						 
						input.seek(index28_126);
						if ( s>=0 ) return s;
						break;

					case 49 : 
						int LA28_256 = input.LA(1);
						 
						int index28_256 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_256==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_256);
						if ( s>=0 ) return s;
						break;

					case 50 : 
						int LA28_128 = input.LA(1);
						 
						int index28_128 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_128==IN) && ((strict == false))) {s = 129;}
						else if ( (LA28_128==LIKE) && ((strict == false))) {s = 130;}
						 
						input.seek(index28_128);
						if ( s>=0 ) return s;
						break;

					case 51 : 
						int LA28_257 = input.LA(1);
						 
						int index28_257 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_257==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_257);
						if ( s>=0 ) return s;
						break;

					case 52 : 
						int LA28_83 = input.LA(1);
						 
						int index28_83 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_83==RPAREN) ) {s = 166;}
						else if ( (LA28_83==ID) && ((strict == false))) {s = 134;}
						else if ( (LA28_83==DOUBLE_QUOTE) && ((strict == false))) {s = 135;}
						else if ( (LA28_83==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_83==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_83==QUOTED_STRING) && ((strict == false))) {s = 136;}
						else if ( (LA28_83==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_83==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_83==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_83==COLON) && ((strict == false))) {s = 89;}
						 
						input.seek(index28_83);
						if ( s>=0 ) return s;
						break;

					case 53 : 
						int LA28_274 = input.LA(1);
						 
						int index28_274 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_274==EQUALS||(LA28_274 >= GREATERTHAN && LA28_274 <= GREATERTHANOREQUALS)||(LA28_274 >= LESSTHAN && LA28_274 <= LESSTHANOREQUALS)||LA28_274==NOTEQUALS) && ((strict == false))) {s = 127;}
						else if ( (LA28_274==NOT) && ((strict == false))) {s = 128;}
						else if ( (LA28_274==IN) && ((strict == false))) {s = 129;}
						else if ( (LA28_274==LIKE) && ((strict == false))) {s = 130;}
						else if ( (LA28_274==IS) && ((strict == false))) {s = 131;}
						 
						input.seek(index28_274);
						if ( s>=0 ) return s;
						break;

					case 54 : 
						int LA28_265 = input.LA(1);
						 
						int index28_265 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_265==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_265);
						if ( s>=0 ) return s;
						break;

					case 55 : 
						int LA28_69 = input.LA(1);
						 
						int index28_69 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_69==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_69);
						if ( s>=0 ) return s;
						break;

					case 56 : 
						int LA28_66 = input.LA(1);
						 
						int index28_66 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_66==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_66);
						if ( s>=0 ) return s;
						break;

					case 57 : 
						int LA28_266 = input.LA(1);
						 
						int index28_266 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_266==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_266);
						if ( s>=0 ) return s;
						break;

					case 58 : 
						int LA28_2 = input.LA(1);
						 
						int index28_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_2==SELECT) && ((strict == false))) {s = 42;}
						else if ( (LA28_2==AS) && ((strict == false))) {s = 43;}
						else if ( (LA28_2==FROM) && ((strict == false))) {s = 44;}
						else if ( (LA28_2==JOIN) && ((strict == false))) {s = 45;}
						else if ( (LA28_2==INNER) && ((strict == false))) {s = 46;}
						else if ( (LA28_2==LEFT) && ((strict == false))) {s = 47;}
						else if ( (LA28_2==OUTER) && ((strict == false))) {s = 48;}
						else if ( (LA28_2==ON) && ((strict == false))) {s = 49;}
						else if ( (LA28_2==WHERE) && ((strict == false))) {s = 50;}
						else if ( (LA28_2==OR) && ((strict == false))) {s = 51;}
						else if ( (LA28_2==AND) && ((strict == false))) {s = 52;}
						else if ( (LA28_2==NOT) && ((strict == false))) {s = 53;}
						else if ( (LA28_2==IN) && ((strict == false))) {s = 54;}
						else if ( (LA28_2==LIKE) && ((strict == false))) {s = 55;}
						else if ( (LA28_2==IS) && ((strict == false))) {s = 56;}
						else if ( (LA28_2==NULL) && ((strict == false))) {s = 57;}
						else if ( (LA28_2==ANY) && ((strict == false))) {s = 58;}
						else if ( (LA28_2==CONTAINS) && ((strict == false))) {s = 59;}
						else if ( (LA28_2==IN_FOLDER) && ((strict == false))) {s = 60;}
						else if ( (LA28_2==IN_TREE) && ((strict == false))) {s = 61;}
						else if ( (LA28_2==ORDER) && ((strict == false))) {s = 62;}
						else if ( (LA28_2==BY) && ((strict == false))) {s = 63;}
						else if ( (LA28_2==ASC) && ((strict == false))) {s = 64;}
						else if ( (LA28_2==DESC) && ((strict == false))) {s = 65;}
						else if ( (LA28_2==TIMESTAMP) && ((strict == false))) {s = 66;}
						else if ( (LA28_2==TRUE) && ((strict == false))) {s = 67;}
						else if ( (LA28_2==FALSE) && ((strict == false))) {s = 68;}
						else if ( (LA28_2==SCORE) && ((strict == false))) {s = 69;}
						else if ( (LA28_2==ID) && ((strict == false))) {s = 70;}
						 
						input.seek(index28_2);
						if ( s>=0 ) return s;
						break;

					case 59 : 
						int LA28_48 = input.LA(1);
						 
						int index28_48 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_48==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_48);
						if ( s>=0 ) return s;
						break;

					case 60 : 
						int LA28_267 = input.LA(1);
						 
						int index28_267 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_267==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_267);
						if ( s>=0 ) return s;
						break;

					case 61 : 
						int LA28_49 = input.LA(1);
						 
						int index28_49 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_49==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_49);
						if ( s>=0 ) return s;
						break;

					case 62 : 
						int LA28_65 = input.LA(1);
						 
						int index28_65 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_65==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_65);
						if ( s>=0 ) return s;
						break;

					case 63 : 
						int LA28_262 = input.LA(1);
						 
						int index28_262 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_262==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_262);
						if ( s>=0 ) return s;
						break;

					case 64 : 
						int LA28_263 = input.LA(1);
						 
						int index28_263 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_263==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_263);
						if ( s>=0 ) return s;
						break;

					case 65 : 
						int LA28_264 = input.LA(1);
						 
						int index28_264 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_264==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_264);
						if ( s>=0 ) return s;
						break;

					case 66 : 
						int LA28_1 = input.LA(1);
						 
						int index28_1 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_1==DOT) ) {s = 35;}
						else if ( (LA28_1==EQUALS||(LA28_1 >= GREATERTHAN && LA28_1 <= GREATERTHANOREQUALS)||(LA28_1 >= LESSTHAN && LA28_1 <= LESSTHANOREQUALS)||LA28_1==NOTEQUALS) ) {s = 36;}
						else if ( (LA28_1==LPAREN) && ((strict == false))) {s = 37;}
						else if ( (LA28_1==NOT) ) {s = 38;}
						else if ( (LA28_1==IN) ) {s = 39;}
						else if ( (LA28_1==LIKE) ) {s = 40;}
						else if ( (LA28_1==IS) ) {s = 41;}
						 
						input.seek(index28_1);
						if ( s>=0 ) return s;
						break;

					case 67 : 
						int LA28_271 = input.LA(1);
						 
						int index28_271 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_271==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_271);
						if ( s>=0 ) return s;
						break;

					case 68 : 
						int LA28_42 = input.LA(1);
						 
						int index28_42 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_42==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_42);
						if ( s>=0 ) return s;
						break;

					case 69 : 
						int LA28_64 = input.LA(1);
						 
						int index28_64 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_64==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_64);
						if ( s>=0 ) return s;
						break;

					case 70 : 
						int LA28_46 = input.LA(1);
						 
						int index28_46 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_46==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_46);
						if ( s>=0 ) return s;
						break;

					case 71 : 
						int LA28_269 = input.LA(1);
						 
						int index28_269 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_269==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_269);
						if ( s>=0 ) return s;
						break;

					case 72 : 
						int LA28_268 = input.LA(1);
						 
						int index28_268 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_268==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_268);
						if ( s>=0 ) return s;
						break;

					case 73 : 
						int LA28_270 = input.LA(1);
						 
						int index28_270 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_270==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_270);
						if ( s>=0 ) return s;
						break;

					case 74 : 
						int LA28_43 = input.LA(1);
						 
						int index28_43 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_43==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_43);
						if ( s>=0 ) return s;
						break;

					case 75 : 
						int LA28_47 = input.LA(1);
						 
						int index28_47 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_47==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_47);
						if ( s>=0 ) return s;
						break;

					case 76 : 
						int LA28_63 = input.LA(1);
						 
						int index28_63 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_63==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_63);
						if ( s>=0 ) return s;
						break;

					case 77 : 
						int LA28_70 = input.LA(1);
						 
						int index28_70 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_70==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_70);
						if ( s>=0 ) return s;
						break;

					case 78 : 
						int LA28_272 = input.LA(1);
						 
						int index28_272 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_272==DOUBLE_QUOTE) && ((strict == false))) {s = 274;}
						 
						input.seek(index28_272);
						if ( s>=0 ) return s;
						break;

					case 79 : 
						int LA28_44 = input.LA(1);
						 
						int index28_44 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_44==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_44);
						if ( s>=0 ) return s;
						break;

					case 80 : 
						int LA28_73 = input.LA(1);
						 
						int index28_73 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_73==ID) ) {s = 81;}
						else if ( (LA28_73==DOUBLE_QUOTE) ) {s = 82;}
						else if ( (LA28_73==QUOTED_STRING) ) {s = 83;}
						else if ( (LA28_73==FLOATING_POINT_LITERAL) && ((strict == false))) {s = 84;}
						else if ( (LA28_73==DECIMAL_INTEGER_LITERAL) && ((strict == false))) {s = 85;}
						else if ( (LA28_73==TRUE) && ((strict == false))) {s = 86;}
						else if ( (LA28_73==FALSE) && ((strict == false))) {s = 87;}
						else if ( (LA28_73==TIMESTAMP) && ((strict == false))) {s = 88;}
						else if ( (LA28_73==COLON) && ((strict == false))) {s = 89;}
						else if ( (LA28_73==RPAREN) && ((strict == false))) {s = 90;}
						 
						input.seek(index28_73);
						if ( s>=0 ) return s;
						break;

					case 81 : 
						int LA28_62 = input.LA(1);
						 
						int index28_62 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_62==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_62);
						if ( s>=0 ) return s;
						break;

					case 82 : 
						int LA28_166 = input.LA(1);
						 
						int index28_166 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_166==AND) ) {s = 233;}
						else if ( (LA28_166==OR) ) {s = 234;}
						else if ( (LA28_166==ORDER) ) {s = 235;}
						else if ( (LA28_166==EOF) ) {s = 236;}
						else if ( (LA28_166==RPAREN) ) {s = 237;}
						else if ( (LA28_166==EQUALS||(LA28_166 >= GREATERTHAN && LA28_166 <= GREATERTHANOREQUALS)||(LA28_166 >= LESSTHAN && LA28_166 <= LESSTHANOREQUALS)||LA28_166==NOTEQUALS) && ((strict == false))) {s = 127;}
						 
						input.seek(index28_166);
						if ( s>=0 ) return s;
						break;

					case 83 : 
						int LA28_80 = input.LA(1);
						 
						int index28_80 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_80==DOT) && ((strict == false))) {s = 126;}
						else if ( (LA28_80==EQUALS||(LA28_80 >= GREATERTHAN && LA28_80 <= GREATERTHANOREQUALS)||(LA28_80 >= LESSTHAN && LA28_80 <= LESSTHANOREQUALS)||LA28_80==NOTEQUALS) && ((strict == false))) {s = 127;}
						else if ( (LA28_80==NOT) && ((strict == false))) {s = 128;}
						else if ( (LA28_80==IN) && ((strict == false))) {s = 129;}
						else if ( (LA28_80==LIKE) && ((strict == false))) {s = 130;}
						else if ( (LA28_80==IS) && ((strict == false))) {s = 131;}
						 
						input.seek(index28_80);
						if ( s>=0 ) return s;
						break;

					case 84 : 
						int LA28_45 = input.LA(1);
						 
						int index28_45 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_45==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_45);
						if ( s>=0 ) return s;
						break;

					case 85 : 
						int LA28_61 = input.LA(1);
						 
						int index28_61 = input.index();
						input.rewind();
						s = -1;
						if ( (LA28_61==DOUBLE_QUOTE) && ((strict == false))) {s = 80;}
						 
						input.seek(index28_61);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 28, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA34_eotS =
		"\151\uffff";
	static final String DFA34_eofS =
		"\151\uffff";
	static final String DFA34_minS =
		"\1\33\1\30\1\5\1\33\1\65\35\33\1\52\1\5\2\uffff\1\30\36\33\1\65\2\52\1"+
		"\5\2\uffff\35\33\1\52";
	static final String DFA34_maxS =
		"\1\45\1\52\1\127\1\45\1\67\35\33\1\52\1\127\2\uffff\1\52\35\33\1\45\1"+
		"\67\2\52\1\127\2\uffff\35\33\1\52";
	static final String DFA34_acceptS =
		"\44\uffff\1\1\1\2\43\uffff\1\1\1\2\36\uffff";
	static final String DFA34_specialS =
		"\1\46\1\uffff\1\50\2\uffff\1\35\1\26\1\31\1\33\1\44\1\47\1\45\1\37\1\42"+
		"\1\73\1\72\1\75\1\57\1\65\1\63\1\7\1\1\1\20\1\24\1\14\1\32\1\27\1\30\1"+
		"\36\1\34\1\43\1\41\1\52\1\71\4\uffff\1\40\35\uffff\1\16\1\66\1\uffff\1"+
		"\62\1\25\2\uffff\1\12\1\15\1\13\1\21\1\17\1\23\1\22\1\2\1\0\1\4\1\3\1"+
		"\6\1\5\1\11\1\10\1\70\1\67\1\64\1\61\1\60\1\55\1\54\1\51\1\101\1\77\1"+
		"\76\1\74\1\100\1\56\1\53}>";
	static final String[] DFA34_transitionS = {
			"\1\2\11\uffff\1\1",
			"\1\3\21\uffff\1\4",
			"\1\17\1\25\1\6\1\33\1\uffff\1\32\6\uffff\1\26\3\uffff\1\34\11\uffff"+
			"\1\37\1\uffff\1\7\3\uffff\1\41\1\21\1\11\1\27\1\30\1\23\1\10\1\12\2\uffff"+
			"\1\22\5\uffff\1\20\1\uffff\1\24\1\uffff\1\14\1\16\1\31\1\13\15\uffff"+
			"\1\40\1\5\11\uffff\1\35\1\36\1\15",
			"\1\43\11\uffff\1\42",
			"\1\45\1\uffff\1\44",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\46",
			"\1\4",
			"\1\61\1\67\1\50\1\75\1\uffff\1\74\6\uffff\1\70\3\uffff\1\76\11\uffff"+
			"\1\101\1\uffff\1\51\3\uffff\1\103\1\63\1\53\1\71\1\72\1\65\1\52\1\54"+
			"\2\uffff\1\64\5\uffff\1\62\1\uffff\1\66\1\uffff\1\56\1\60\1\73\1\55\15"+
			"\uffff\1\102\1\47\11\uffff\1\77\1\100\1\57",
			"",
			"",
			"\1\104\21\uffff\1\105",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\106",
			"\1\110\11\uffff\1\107",
			"\1\112\1\uffff\1\111",
			"\1\4",
			"\1\105",
			"\1\125\1\133\1\114\1\141\1\uffff\1\140\6\uffff\1\134\3\uffff\1\142\11"+
			"\uffff\1\145\1\uffff\1\115\3\uffff\1\147\1\127\1\117\1\135\1\136\1\131"+
			"\1\116\1\120\2\uffff\1\130\5\uffff\1\126\1\uffff\1\132\1\uffff\1\122"+
			"\1\124\1\137\1\121\15\uffff\1\146\1\113\11\uffff\1\143\1\144\1\123",
			"",
			"",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\150",
			"\1\105"
	};

	static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
	static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
	static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
	static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
	static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
	static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
	static final short[][] DFA34_transition;

	static {
		int numStates = DFA34_transitionS.length;
		DFA34_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
		}
	}

	protected class DFA34 extends DFA {

		public DFA34(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 34;
			this.eot = DFA34_eot;
			this.eof = DFA34_eof;
			this.min = DFA34_min;
			this.max = DFA34_max;
			this.accept = DFA34_accept;
			this.special = DFA34_special;
			this.transition = DFA34_transition;
		}
		@Override
		public String getDescription() {
			return "634:1: nullPredicate : ( columnReference IS NULL -> ^( PRED_EXISTS columnReference NOT ) | columnReference IS NOT NULL -> ^( PRED_EXISTS columnReference ) );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA34_83 = input.LA(1);
						 
						int index34_83 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_83==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_83);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA34_21 = input.LA(1);
						 
						int index34_21 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_21==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_21);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA34_82 = input.LA(1);
						 
						int index34_82 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_82==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_82);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA34_85 = input.LA(1);
						 
						int index34_85 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_85==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_85);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA34_84 = input.LA(1);
						 
						int index34_84 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_84==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_84);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA34_87 = input.LA(1);
						 
						int index34_87 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_87==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_87);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA34_86 = input.LA(1);
						 
						int index34_86 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_86==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_86);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA34_20 = input.LA(1);
						 
						int index34_20 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_20==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_20);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA34_89 = input.LA(1);
						 
						int index34_89 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_89==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_89);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA34_88 = input.LA(1);
						 
						int index34_88 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_88==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_88);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA34_75 = input.LA(1);
						 
						int index34_75 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_75==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_75);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA34_77 = input.LA(1);
						 
						int index34_77 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_77==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_77);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA34_24 = input.LA(1);
						 
						int index34_24 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_24==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_24);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA34_76 = input.LA(1);
						 
						int index34_76 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_76==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_76);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA34_68 = input.LA(1);
						 
						int index34_68 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_68==ID) && ((strict == false))) {s = 71;}
						else if ( (LA34_68==DOUBLE_QUOTE) && ((strict == false))) {s = 72;}
						 
						input.seek(index34_68);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA34_79 = input.LA(1);
						 
						int index34_79 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_79==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_79);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA34_22 = input.LA(1);
						 
						int index34_22 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_22==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_22);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA34_78 = input.LA(1);
						 
						int index34_78 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_78==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_78);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA34_81 = input.LA(1);
						 
						int index34_81 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_81==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_81);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA34_80 = input.LA(1);
						 
						int index34_80 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_80==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_80);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA34_23 = input.LA(1);
						 
						int index34_23 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_23==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_23);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA34_72 = input.LA(1);
						 
						int index34_72 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_72==SELECT) && ((strict == false))) {s = 75;}
						else if ( (LA34_72==AS) && ((strict == false))) {s = 76;}
						else if ( (LA34_72==FROM) && ((strict == false))) {s = 77;}
						else if ( (LA34_72==JOIN) && ((strict == false))) {s = 78;}
						else if ( (LA34_72==INNER) && ((strict == false))) {s = 79;}
						else if ( (LA34_72==LEFT) && ((strict == false))) {s = 80;}
						else if ( (LA34_72==OUTER) && ((strict == false))) {s = 81;}
						else if ( (LA34_72==ON) && ((strict == false))) {s = 82;}
						else if ( (LA34_72==WHERE) && ((strict == false))) {s = 83;}
						else if ( (LA34_72==OR) && ((strict == false))) {s = 84;}
						else if ( (LA34_72==AND) && ((strict == false))) {s = 85;}
						else if ( (LA34_72==NOT) && ((strict == false))) {s = 86;}
						else if ( (LA34_72==IN) && ((strict == false))) {s = 87;}
						else if ( (LA34_72==LIKE) && ((strict == false))) {s = 88;}
						else if ( (LA34_72==IS) && ((strict == false))) {s = 89;}
						else if ( (LA34_72==NULL) && ((strict == false))) {s = 90;}
						else if ( (LA34_72==ANY) && ((strict == false))) {s = 91;}
						else if ( (LA34_72==CONTAINS) && ((strict == false))) {s = 92;}
						else if ( (LA34_72==IN_FOLDER) && ((strict == false))) {s = 93;}
						else if ( (LA34_72==IN_TREE) && ((strict == false))) {s = 94;}
						else if ( (LA34_72==ORDER) && ((strict == false))) {s = 95;}
						else if ( (LA34_72==BY) && ((strict == false))) {s = 96;}
						else if ( (LA34_72==ASC) && ((strict == false))) {s = 97;}
						else if ( (LA34_72==DESC) && ((strict == false))) {s = 98;}
						else if ( (LA34_72==TIMESTAMP) && ((strict == false))) {s = 99;}
						else if ( (LA34_72==TRUE) && ((strict == false))) {s = 100;}
						else if ( (LA34_72==FALSE) && ((strict == false))) {s = 101;}
						else if ( (LA34_72==SCORE) && ((strict == false))) {s = 102;}
						else if ( (LA34_72==ID) && ((strict == false))) {s = 103;}
						 
						input.seek(index34_72);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA34_6 = input.LA(1);
						 
						int index34_6 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_6==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_6);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA34_26 = input.LA(1);
						 
						int index34_26 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_26==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_26);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA34_27 = input.LA(1);
						 
						int index34_27 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_27==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_27);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA34_7 = input.LA(1);
						 
						int index34_7 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_7==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_7);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA34_25 = input.LA(1);
						 
						int index34_25 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_25==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_25);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA34_8 = input.LA(1);
						 
						int index34_8 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_8==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_8);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA34_29 = input.LA(1);
						 
						int index34_29 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_29==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_29);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA34_5 = input.LA(1);
						 
						int index34_5 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_5==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_5);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA34_28 = input.LA(1);
						 
						int index34_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_28==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_28);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA34_12 = input.LA(1);
						 
						int index34_12 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_12==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_12);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA34_38 = input.LA(1);
						 
						int index34_38 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_38==DOT) && ((strict == false))) {s = 68;}
						else if ( (LA34_38==IS) && ((strict == false))) {s = 69;}
						 
						input.seek(index34_38);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA34_31 = input.LA(1);
						 
						int index34_31 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_31==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_31);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA34_13 = input.LA(1);
						 
						int index34_13 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_13==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_13);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA34_30 = input.LA(1);
						 
						int index34_30 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_30==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_30);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA34_9 = input.LA(1);
						 
						int index34_9 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_9==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_9);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA34_11 = input.LA(1);
						 
						int index34_11 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_11==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_11);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA34_0 = input.LA(1);
						 
						int index34_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_0==ID) ) {s = 1;}
						else if ( (LA34_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						 
						input.seek(index34_0);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA34_10 = input.LA(1);
						 
						int index34_10 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_10==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_10);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA34_2 = input.LA(1);
						 
						int index34_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_2==SELECT) && ((strict == false))) {s = 5;}
						else if ( (LA34_2==AS) && ((strict == false))) {s = 6;}
						else if ( (LA34_2==FROM) && ((strict == false))) {s = 7;}
						else if ( (LA34_2==JOIN) && ((strict == false))) {s = 8;}
						else if ( (LA34_2==INNER) && ((strict == false))) {s = 9;}
						else if ( (LA34_2==LEFT) && ((strict == false))) {s = 10;}
						else if ( (LA34_2==OUTER) && ((strict == false))) {s = 11;}
						else if ( (LA34_2==ON) && ((strict == false))) {s = 12;}
						else if ( (LA34_2==WHERE) && ((strict == false))) {s = 13;}
						else if ( (LA34_2==OR) && ((strict == false))) {s = 14;}
						else if ( (LA34_2==AND) && ((strict == false))) {s = 15;}
						else if ( (LA34_2==NOT) && ((strict == false))) {s = 16;}
						else if ( (LA34_2==IN) && ((strict == false))) {s = 17;}
						else if ( (LA34_2==LIKE) && ((strict == false))) {s = 18;}
						else if ( (LA34_2==IS) && ((strict == false))) {s = 19;}
						else if ( (LA34_2==NULL) && ((strict == false))) {s = 20;}
						else if ( (LA34_2==ANY) && ((strict == false))) {s = 21;}
						else if ( (LA34_2==CONTAINS) && ((strict == false))) {s = 22;}
						else if ( (LA34_2==IN_FOLDER) && ((strict == false))) {s = 23;}
						else if ( (LA34_2==IN_TREE) && ((strict == false))) {s = 24;}
						else if ( (LA34_2==ORDER) && ((strict == false))) {s = 25;}
						else if ( (LA34_2==BY) && ((strict == false))) {s = 26;}
						else if ( (LA34_2==ASC) && ((strict == false))) {s = 27;}
						else if ( (LA34_2==DESC) && ((strict == false))) {s = 28;}
						else if ( (LA34_2==TIMESTAMP) && ((strict == false))) {s = 29;}
						else if ( (LA34_2==TRUE) && ((strict == false))) {s = 30;}
						else if ( (LA34_2==FALSE) && ((strict == false))) {s = 31;}
						else if ( (LA34_2==SCORE) && ((strict == false))) {s = 32;}
						else if ( (LA34_2==ID) && ((strict == false))) {s = 33;}
						 
						input.seek(index34_2);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA34_97 = input.LA(1);
						 
						int index34_97 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_97==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_97);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA34_32 = input.LA(1);
						 
						int index34_32 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_32==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_32);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA34_104 = input.LA(1);
						 
						int index34_104 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_104==IS) && ((strict == false))) {s = 69;}
						 
						input.seek(index34_104);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA34_96 = input.LA(1);
						 
						int index34_96 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_96==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_96);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA34_95 = input.LA(1);
						 
						int index34_95 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_95==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_95);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA34_103 = input.LA(1);
						 
						int index34_103 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_103==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_103);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA34_17 = input.LA(1);
						 
						int index34_17 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_17==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_17);
						if ( s>=0 ) return s;
						break;

					case 48 : 
						int LA34_94 = input.LA(1);
						 
						int index34_94 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_94==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_94);
						if ( s>=0 ) return s;
						break;

					case 49 : 
						int LA34_93 = input.LA(1);
						 
						int index34_93 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_93==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_93);
						if ( s>=0 ) return s;
						break;

					case 50 : 
						int LA34_71 = input.LA(1);
						 
						int index34_71 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_71==IS) && ((strict == false))) {s = 69;}
						 
						input.seek(index34_71);
						if ( s>=0 ) return s;
						break;

					case 51 : 
						int LA34_19 = input.LA(1);
						 
						int index34_19 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_19==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_19);
						if ( s>=0 ) return s;
						break;

					case 52 : 
						int LA34_92 = input.LA(1);
						 
						int index34_92 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_92==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_92);
						if ( s>=0 ) return s;
						break;

					case 53 : 
						int LA34_18 = input.LA(1);
						 
						int index34_18 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_18==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_18);
						if ( s>=0 ) return s;
						break;

					case 54 : 
						int LA34_69 = input.LA(1);
						 
						int index34_69 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_69==NULL) && ((strict == false))) {s = 73;}
						else if ( (LA34_69==NOT) && ((strict == false))) {s = 74;}
						 
						input.seek(index34_69);
						if ( s>=0 ) return s;
						break;

					case 55 : 
						int LA34_91 = input.LA(1);
						 
						int index34_91 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_91==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_91);
						if ( s>=0 ) return s;
						break;

					case 56 : 
						int LA34_90 = input.LA(1);
						 
						int index34_90 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_90==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_90);
						if ( s>=0 ) return s;
						break;

					case 57 : 
						int LA34_33 = input.LA(1);
						 
						int index34_33 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_33==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_33);
						if ( s>=0 ) return s;
						break;

					case 58 : 
						int LA34_15 = input.LA(1);
						 
						int index34_15 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_15==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_15);
						if ( s>=0 ) return s;
						break;

					case 59 : 
						int LA34_14 = input.LA(1);
						 
						int index34_14 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_14==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_14);
						if ( s>=0 ) return s;
						break;

					case 60 : 
						int LA34_101 = input.LA(1);
						 
						int index34_101 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_101==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_101);
						if ( s>=0 ) return s;
						break;

					case 61 : 
						int LA34_16 = input.LA(1);
						 
						int index34_16 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_16==DOUBLE_QUOTE) && ((strict == false))) {s = 38;}
						 
						input.seek(index34_16);
						if ( s>=0 ) return s;
						break;

					case 62 : 
						int LA34_100 = input.LA(1);
						 
						int index34_100 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_100==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_100);
						if ( s>=0 ) return s;
						break;

					case 63 : 
						int LA34_99 = input.LA(1);
						 
						int index34_99 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_99==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_99);
						if ( s>=0 ) return s;
						break;

					case 64 : 
						int LA34_102 = input.LA(1);
						 
						int index34_102 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_102==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_102);
						if ( s>=0 ) return s;
						break;

					case 65 : 
						int LA34_98 = input.LA(1);
						 
						int index34_98 = input.index();
						input.rewind();
						s = -1;
						if ( (LA34_98==DOUBLE_QUOTE) && ((strict == false))) {s = 104;}
						 
						input.seek(index34_98);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 34, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA41_eotS =
		"\153\uffff";
	static final String DFA41_eofS =
		"\1\uffff\1\5\43\uffff\1\5\1\uffff\1\107\42\uffff\1\5\1\107\36\uffff\1"+
		"\107";
	static final String DFA41_minS =
		"\1\33\1\10\1\5\1\33\4\uffff\35\33\1\10\1\5\1\10\36\33\4\uffff\2\10\1\5"+
		"\35\33\1\10";
	static final String DFA41_maxS =
		"\1\45\1\30\1\127\1\45\4\uffff\35\33\1\25\1\127\1\30\35\33\1\45\4\uffff"+
		"\2\25\1\127\35\33\1\25";
	static final String DFA41_acceptS =
		"\4\uffff\2\1\2\2\76\uffff\2\1\2\2\41\uffff";
	static final String DFA41_specialS =
		"\1\43\1\uffff\1\52\5\uffff\1\36\1\26\1\32\1\34\1\47\1\51\1\50\1\42\1\45"+
		"\1\72\1\71\1\74\1\60\1\65\1\63\1\7\1\1\1\20\1\24\1\14\1\33\1\27\1\31\1"+
		"\40\1\35\1\46\1\44\1\54\1\70\2\uffff\1\30\35\uffff\1\16\5\uffff\1\37\1"+
		"\25\1\12\1\15\1\13\1\21\1\17\1\23\1\22\1\2\1\0\1\4\1\3\1\6\1\5\1\11\1"+
		"\10\1\67\1\66\1\64\1\62\1\61\1\56\1\55\1\53\1\100\1\76\1\75\1\73\1\77"+
		"\1\57\1\41}>";
	static final String[] DFA41_transitionS = {
			"\1\2\11\uffff\1\1",
			"\1\6\6\uffff\1\4\5\uffff\1\7\2\uffff\1\3",
			"\1\22\1\30\1\11\1\36\1\uffff\1\35\6\uffff\1\31\3\uffff\1\37\11\uffff"+
			"\1\42\1\uffff\1\12\3\uffff\1\44\1\24\1\14\1\32\1\33\1\26\1\13\1\15\2"+
			"\uffff\1\25\5\uffff\1\23\1\uffff\1\27\1\uffff\1\17\1\21\1\34\1\16\15"+
			"\uffff\1\43\1\10\11\uffff\1\40\1\41\1\20",
			"\1\46\11\uffff\1\45",
			"",
			"",
			"",
			"",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\47",
			"\1\6\6\uffff\1\4\5\uffff\1\7",
			"\1\62\1\70\1\51\1\76\1\uffff\1\75\6\uffff\1\71\3\uffff\1\77\11\uffff"+
			"\1\102\1\uffff\1\52\3\uffff\1\104\1\64\1\54\1\72\1\73\1\66\1\53\1\55"+
			"\2\uffff\1\65\5\uffff\1\63\1\uffff\1\67\1\uffff\1\57\1\61\1\74\1\56\15"+
			"\uffff\1\103\1\50\11\uffff\1\100\1\101\1\60",
			"\1\110\6\uffff\1\106\5\uffff\1\111\2\uffff\1\105",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\112",
			"\1\114\11\uffff\1\113",
			"",
			"",
			"",
			"",
			"\1\6\6\uffff\1\4\5\uffff\1\7",
			"\1\110\6\uffff\1\106\5\uffff\1\111",
			"\1\127\1\135\1\116\1\143\1\uffff\1\142\6\uffff\1\136\3\uffff\1\144\11"+
			"\uffff\1\147\1\uffff\1\117\3\uffff\1\151\1\131\1\121\1\137\1\140\1\133"+
			"\1\120\1\122\2\uffff\1\132\5\uffff\1\130\1\uffff\1\134\1\uffff\1\124"+
			"\1\126\1\141\1\123\15\uffff\1\150\1\115\11\uffff\1\145\1\146\1\125",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\152",
			"\1\110\6\uffff\1\106\5\uffff\1\111"
	};

	static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
	static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
	static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
	static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
	static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
	static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
	static final short[][] DFA41_transition;

	static {
		int numStates = DFA41_transitionS.length;
		DFA41_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
		}
	}

	protected class DFA41 extends DFA {

		public DFA41(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 41;
			this.eot = DFA41_eot;
			this.eof = DFA41_eof;
			this.min = DFA41_min;
			this.max = DFA41_max;
			this.accept = DFA41_accept;
			this.special = DFA41_special;
			this.transition = DFA41_transition;
		}
		@Override
		public String getDescription() {
			return "696:1: sortSpecification : ( columnReference -> ^( SORT_SPECIFICATION columnReference ASC ) | columnReference (by= ASC |by= DESC ) -> ^( SORT_SPECIFICATION columnReference $by) );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA41_85 = input.LA(1);
						 
						int index41_85 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_85==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_85);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA41_24 = input.LA(1);
						 
						int index41_24 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_24==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_24);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA41_84 = input.LA(1);
						 
						int index41_84 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_84==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_84);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA41_87 = input.LA(1);
						 
						int index41_87 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_87==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_87);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA41_86 = input.LA(1);
						 
						int index41_86 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_86==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_86);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA41_89 = input.LA(1);
						 
						int index41_89 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_89==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_89);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA41_88 = input.LA(1);
						 
						int index41_88 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_88==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_88);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA41_23 = input.LA(1);
						 
						int index41_23 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_23==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_23);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA41_91 = input.LA(1);
						 
						int index41_91 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_91==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_91);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA41_90 = input.LA(1);
						 
						int index41_90 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_90==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_90);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA41_77 = input.LA(1);
						 
						int index41_77 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_77==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_77);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA41_79 = input.LA(1);
						 
						int index41_79 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_79==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_79);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA41_27 = input.LA(1);
						 
						int index41_27 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_27==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_27);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA41_78 = input.LA(1);
						 
						int index41_78 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_78==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_78);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA41_69 = input.LA(1);
						 
						int index41_69 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_69==ID) && ((strict == false))) {s = 75;}
						else if ( (LA41_69==DOUBLE_QUOTE) && ((strict == false))) {s = 76;}
						 
						input.seek(index41_69);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA41_81 = input.LA(1);
						 
						int index41_81 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_81==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_81);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA41_25 = input.LA(1);
						 
						int index41_25 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_25==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_25);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA41_80 = input.LA(1);
						 
						int index41_80 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_80==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_80);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA41_83 = input.LA(1);
						 
						int index41_83 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_83==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_83);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA41_82 = input.LA(1);
						 
						int index41_82 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_82==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_82);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA41_26 = input.LA(1);
						 
						int index41_26 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_26==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_26);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA41_76 = input.LA(1);
						 
						int index41_76 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_76==SELECT) && ((strict == false))) {s = 77;}
						else if ( (LA41_76==AS) && ((strict == false))) {s = 78;}
						else if ( (LA41_76==FROM) && ((strict == false))) {s = 79;}
						else if ( (LA41_76==JOIN) && ((strict == false))) {s = 80;}
						else if ( (LA41_76==INNER) && ((strict == false))) {s = 81;}
						else if ( (LA41_76==LEFT) && ((strict == false))) {s = 82;}
						else if ( (LA41_76==OUTER) && ((strict == false))) {s = 83;}
						else if ( (LA41_76==ON) && ((strict == false))) {s = 84;}
						else if ( (LA41_76==WHERE) && ((strict == false))) {s = 85;}
						else if ( (LA41_76==OR) && ((strict == false))) {s = 86;}
						else if ( (LA41_76==AND) && ((strict == false))) {s = 87;}
						else if ( (LA41_76==NOT) && ((strict == false))) {s = 88;}
						else if ( (LA41_76==IN) && ((strict == false))) {s = 89;}
						else if ( (LA41_76==LIKE) && ((strict == false))) {s = 90;}
						else if ( (LA41_76==IS) && ((strict == false))) {s = 91;}
						else if ( (LA41_76==NULL) && ((strict == false))) {s = 92;}
						else if ( (LA41_76==ANY) && ((strict == false))) {s = 93;}
						else if ( (LA41_76==CONTAINS) && ((strict == false))) {s = 94;}
						else if ( (LA41_76==IN_FOLDER) && ((strict == false))) {s = 95;}
						else if ( (LA41_76==IN_TREE) && ((strict == false))) {s = 96;}
						else if ( (LA41_76==ORDER) && ((strict == false))) {s = 97;}
						else if ( (LA41_76==BY) && ((strict == false))) {s = 98;}
						else if ( (LA41_76==ASC) && ((strict == false))) {s = 99;}
						else if ( (LA41_76==DESC) && ((strict == false))) {s = 100;}
						else if ( (LA41_76==TIMESTAMP) && ((strict == false))) {s = 101;}
						else if ( (LA41_76==TRUE) && ((strict == false))) {s = 102;}
						else if ( (LA41_76==FALSE) && ((strict == false))) {s = 103;}
						else if ( (LA41_76==SCORE) && ((strict == false))) {s = 104;}
						else if ( (LA41_76==ID) && ((strict == false))) {s = 105;}
						 
						input.seek(index41_76);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA41_9 = input.LA(1);
						 
						int index41_9 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_9==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_9);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA41_29 = input.LA(1);
						 
						int index41_29 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_29==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_29);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA41_39 = input.LA(1);
						 
						int index41_39 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_39==DOT) && ((strict == false))) {s = 69;}
						else if ( (LA41_39==COMMA) && ((strict == false))) {s = 70;}
						else if ( (LA41_39==EOF) && ((strict == false))) {s = 71;}
						else if ( (LA41_39==ASC) && ((strict == false))) {s = 72;}
						else if ( (LA41_39==DESC) && ((strict == false))) {s = 73;}
						 
						input.seek(index41_39);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA41_30 = input.LA(1);
						 
						int index41_30 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_30==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_30);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA41_10 = input.LA(1);
						 
						int index41_10 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_10==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_10);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA41_28 = input.LA(1);
						 
						int index41_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_28==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_28);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA41_11 = input.LA(1);
						 
						int index41_11 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_11==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_11);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA41_32 = input.LA(1);
						 
						int index41_32 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_32==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_32);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA41_8 = input.LA(1);
						 
						int index41_8 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_8==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_8);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA41_75 = input.LA(1);
						 
						int index41_75 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_75==COMMA) && ((strict == false))) {s = 70;}
						else if ( (LA41_75==EOF) && ((strict == false))) {s = 71;}
						else if ( (LA41_75==ASC) && ((strict == false))) {s = 72;}
						else if ( (LA41_75==DESC) && ((strict == false))) {s = 73;}
						 
						input.seek(index41_75);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA41_31 = input.LA(1);
						 
						int index41_31 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_31==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_31);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA41_106 = input.LA(1);
						 
						int index41_106 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_106==COMMA) && ((strict == false))) {s = 70;}
						else if ( (LA41_106==EOF) && ((strict == false))) {s = 71;}
						else if ( (LA41_106==ASC) && ((strict == false))) {s = 72;}
						else if ( (LA41_106==DESC) && ((strict == false))) {s = 73;}
						 
						input.seek(index41_106);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA41_15 = input.LA(1);
						 
						int index41_15 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_15==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_15);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA41_0 = input.LA(1);
						 
						int index41_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_0==ID) ) {s = 1;}
						else if ( (LA41_0==DOUBLE_QUOTE) && ((strict == false))) {s = 2;}
						 
						input.seek(index41_0);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA41_34 = input.LA(1);
						 
						int index41_34 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_34==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_34);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA41_16 = input.LA(1);
						 
						int index41_16 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_16==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_16);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA41_33 = input.LA(1);
						 
						int index41_33 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_33==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_33);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA41_12 = input.LA(1);
						 
						int index41_12 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_12==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_12);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA41_14 = input.LA(1);
						 
						int index41_14 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_14==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_14);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA41_13 = input.LA(1);
						 
						int index41_13 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_13==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_13);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA41_2 = input.LA(1);
						 
						int index41_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_2==SELECT) && ((strict == false))) {s = 8;}
						else if ( (LA41_2==AS) && ((strict == false))) {s = 9;}
						else if ( (LA41_2==FROM) && ((strict == false))) {s = 10;}
						else if ( (LA41_2==JOIN) && ((strict == false))) {s = 11;}
						else if ( (LA41_2==INNER) && ((strict == false))) {s = 12;}
						else if ( (LA41_2==LEFT) && ((strict == false))) {s = 13;}
						else if ( (LA41_2==OUTER) && ((strict == false))) {s = 14;}
						else if ( (LA41_2==ON) && ((strict == false))) {s = 15;}
						else if ( (LA41_2==WHERE) && ((strict == false))) {s = 16;}
						else if ( (LA41_2==OR) && ((strict == false))) {s = 17;}
						else if ( (LA41_2==AND) && ((strict == false))) {s = 18;}
						else if ( (LA41_2==NOT) && ((strict == false))) {s = 19;}
						else if ( (LA41_2==IN) && ((strict == false))) {s = 20;}
						else if ( (LA41_2==LIKE) && ((strict == false))) {s = 21;}
						else if ( (LA41_2==IS) && ((strict == false))) {s = 22;}
						else if ( (LA41_2==NULL) && ((strict == false))) {s = 23;}
						else if ( (LA41_2==ANY) && ((strict == false))) {s = 24;}
						else if ( (LA41_2==CONTAINS) && ((strict == false))) {s = 25;}
						else if ( (LA41_2==IN_FOLDER) && ((strict == false))) {s = 26;}
						else if ( (LA41_2==IN_TREE) && ((strict == false))) {s = 27;}
						else if ( (LA41_2==ORDER) && ((strict == false))) {s = 28;}
						else if ( (LA41_2==BY) && ((strict == false))) {s = 29;}
						else if ( (LA41_2==ASC) && ((strict == false))) {s = 30;}
						else if ( (LA41_2==DESC) && ((strict == false))) {s = 31;}
						else if ( (LA41_2==TIMESTAMP) && ((strict == false))) {s = 32;}
						else if ( (LA41_2==TRUE) && ((strict == false))) {s = 33;}
						else if ( (LA41_2==FALSE) && ((strict == false))) {s = 34;}
						else if ( (LA41_2==SCORE) && ((strict == false))) {s = 35;}
						else if ( (LA41_2==ID) && ((strict == false))) {s = 36;}
						 
						input.seek(index41_2);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA41_99 = input.LA(1);
						 
						int index41_99 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_99==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_99);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA41_35 = input.LA(1);
						 
						int index41_35 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_35==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_35);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA41_98 = input.LA(1);
						 
						int index41_98 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_98==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_98);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA41_97 = input.LA(1);
						 
						int index41_97 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_97==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_97);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA41_105 = input.LA(1);
						 
						int index41_105 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_105==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_105);
						if ( s>=0 ) return s;
						break;

					case 48 : 
						int LA41_20 = input.LA(1);
						 
						int index41_20 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_20==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_20);
						if ( s>=0 ) return s;
						break;

					case 49 : 
						int LA41_96 = input.LA(1);
						 
						int index41_96 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_96==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_96);
						if ( s>=0 ) return s;
						break;

					case 50 : 
						int LA41_95 = input.LA(1);
						 
						int index41_95 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_95==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_95);
						if ( s>=0 ) return s;
						break;

					case 51 : 
						int LA41_22 = input.LA(1);
						 
						int index41_22 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_22==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_22);
						if ( s>=0 ) return s;
						break;

					case 52 : 
						int LA41_94 = input.LA(1);
						 
						int index41_94 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_94==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_94);
						if ( s>=0 ) return s;
						break;

					case 53 : 
						int LA41_21 = input.LA(1);
						 
						int index41_21 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_21==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_21);
						if ( s>=0 ) return s;
						break;

					case 54 : 
						int LA41_93 = input.LA(1);
						 
						int index41_93 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_93==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_93);
						if ( s>=0 ) return s;
						break;

					case 55 : 
						int LA41_92 = input.LA(1);
						 
						int index41_92 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_92==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_92);
						if ( s>=0 ) return s;
						break;

					case 56 : 
						int LA41_36 = input.LA(1);
						 
						int index41_36 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_36==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_36);
						if ( s>=0 ) return s;
						break;

					case 57 : 
						int LA41_18 = input.LA(1);
						 
						int index41_18 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_18==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_18);
						if ( s>=0 ) return s;
						break;

					case 58 : 
						int LA41_17 = input.LA(1);
						 
						int index41_17 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_17==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_17);
						if ( s>=0 ) return s;
						break;

					case 59 : 
						int LA41_103 = input.LA(1);
						 
						int index41_103 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_103==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_103);
						if ( s>=0 ) return s;
						break;

					case 60 : 
						int LA41_19 = input.LA(1);
						 
						int index41_19 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_19==DOUBLE_QUOTE) && ((strict == false))) {s = 39;}
						 
						input.seek(index41_19);
						if ( s>=0 ) return s;
						break;

					case 61 : 
						int LA41_102 = input.LA(1);
						 
						int index41_102 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_102==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_102);
						if ( s>=0 ) return s;
						break;

					case 62 : 
						int LA41_101 = input.LA(1);
						 
						int index41_101 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_101==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_101);
						if ( s>=0 ) return s;
						break;

					case 63 : 
						int LA41_104 = input.LA(1);
						 
						int index41_104 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_104==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_104);
						if ( s>=0 ) return s;
						break;

					case 64 : 
						int LA41_100 = input.LA(1);
						 
						int index41_100 = input.index();
						input.rewind();
						s = -1;
						if ( (LA41_100==DOUBLE_QUOTE) && ((strict == false))) {s = 106;}
						 
						input.seek(index41_100);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 41, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_SELECT_in_query416 = new BitSet(new long[]{0x1EA09FE2882205E0L,0x0000000000E10C00L});
	public static final BitSet FOLLOW_selectList_in_query418 = new BitSet(new long[]{0x0000000200000000L});
	public static final BitSet FOLLOW_fromClause_in_query420 = new BitSet(new long[]{0x0800000000000000L,0x0000000000800000L});
	public static final BitSet FOLLOW_whereClause_in_query422 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_orderByClause_in_query425 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_query428 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAR_in_selectList527 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selectSubList_in_selectList585 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_COMMA_in_selectList588 = new BitSet(new long[]{0x1EA09FE2882205E0L,0x0000000000E00C00L});
	public static final BitSet FOLLOW_selectSubList_in_selectList590 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_valueExpression_in_selectSubList674 = new BitSet(new long[]{0x0000002008000082L});
	public static final BitSet FOLLOW_AS_in_selectSubList677 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnName_in_selectSubList680 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qualifier_in_selectSubList745 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_DOTSTAR_in_selectSubList747 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_columnReference_in_valueExpression838 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_valueFunction_in_valueExpression870 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qualifier_in_columnReference934 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_DOT_in_columnReference936 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnName_in_columnReference940 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisFunction_in_valueFunction1036 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAREN_in_valueFunction1038 = new BitSet(new long[]{0x0000002188080800L,0x0000000000600300L});
	public static final BitSet FOLLOW_functionArgument_in_valueFunction1040 = new BitSet(new long[]{0x0000002188080800L,0x0000000000600300L});
	public static final BitSet FOLLOW_RPAREN_in_valueFunction1043 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_keyWordOrId_in_valueFunction1116 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAREN_in_valueFunction1118 = new BitSet(new long[]{0x0000002188080800L,0x0000000000600300L});
	public static final BitSet FOLLOW_functionArgument_in_valueFunction1120 = new BitSet(new long[]{0x0000002188080800L,0x0000000000600300L});
	public static final BitSet FOLLOW_RPAREN_in_valueFunction1123 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qualifier_in_functionArgument1212 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_DOT_in_functionArgument1214 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnName_in_functionArgument1216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_functionArgument1278 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literalOrParameterName_in_functionArgument1290 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tableName_in_qualifier1329 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_correlationName_in_qualifier1361 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FROM_in_fromClause1424 = new BitSet(new long[]{0x0002002008000000L});
	public static final BitSet FOLLOW_tableReference_in_fromClause1426 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleTable_in_tableReference1489 = new BitSet(new long[]{0x0000188000000002L});
	public static final BitSet FOLLOW_joinedTable_in_tableReference1491 = new BitSet(new long[]{0x0000188000000002L});
	public static final BitSet FOLLOW_simpleTable_in_singleTable1589 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_complexTable_in_singleTable1621 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tableName_in_simpleTable1712 = new BitSet(new long[]{0x0000002008000082L});
	public static final BitSet FOLLOW_AS_in_simpleTable1715 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_correlationName_in_simpleTable1718 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_joinType_in_joinedTable1814 = new BitSet(new long[]{0x0000080000000000L});
	public static final BitSet FOLLOW_JOIN_in_joinedTable1817 = new BitSet(new long[]{0x0002002008000000L});
	public static final BitSet FOLLOW_tableReference_in_joinedTable1819 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_joinSpecification_in_joinedTable1821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_complexTable1930 = new BitSet(new long[]{0x0002002008000000L});
	public static final BitSet FOLLOW_singleTable_in_complexTable1932 = new BitSet(new long[]{0x0000188000000000L});
	public static final BitSet FOLLOW_joinedTable_in_complexTable1934 = new BitSet(new long[]{0x0000188000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_complexTable1937 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_complexTable2000 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_complexTable_in_complexTable2002 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_complexTable2004 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INNER_in_joinType2057 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_in_joinType2089 = new BitSet(new long[]{0x1000000000000002L});
	public static final BitSet FOLLOW_OUTER_in_joinType2091 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ON_in_joinSpecification2155 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnReference_in_joinSpecification2159 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_EQUALS_in_joinSpecification2161 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnReference_in_joinSpecification2165 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WHERE_in_whereClause2265 = new BitSet(new long[]{0x1EA29FE3882A0DE0L,0x0000000000E00D00L});
	public static final BitSet FOLLOW_searchOrCondition_in_whereClause2267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_searchAndCondition_in_searchOrCondition2322 = new BitSet(new long[]{0x0400000000000002L});
	public static final BitSet FOLLOW_OR_in_searchOrCondition2325 = new BitSet(new long[]{0x1EA29FE3882A0DE0L,0x0000000000E00D00L});
	public static final BitSet FOLLOW_searchAndCondition_in_searchOrCondition2327 = new BitSet(new long[]{0x0400000000000002L});
	public static final BitSet FOLLOW_searchNotCondition_in_searchAndCondition2413 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_AND_in_searchAndCondition2416 = new BitSet(new long[]{0x1EA29FE3882A0DE0L,0x0000000000E00D00L});
	public static final BitSet FOLLOW_searchNotCondition_in_searchAndCondition2418 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_NOT_in_searchNotCondition2502 = new BitSet(new long[]{0x1EA29FE3882A0DE0L,0x0000000000E00D00L});
	public static final BitSet FOLLOW_searchTest_in_searchNotCondition2504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_searchTest_in_searchNotCondition2564 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_predicate_in_searchTest2617 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_searchTest2649 = new BitSet(new long[]{0x1EA29FE3882A0DE0L,0x0000000000E00D00L});
	public static final BitSet FOLLOW_searchOrCondition_in_searchTest2651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_searchTest2653 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparisonPredicate_in_predicate2706 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inPredicate_in_predicate2718 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_likePredicate_in_predicate2730 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nullPredicate_in_predicate2742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_quantifiedComparisonPredicate_in_predicate2754 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_quantifiedInPredicate_in_predicate2766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_textSearchPredicate_in_predicate2778 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_folderPredicate_in_predicate2790 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_valueExpression_in_comparisonPredicate2823 = new BitSet(new long[]{0x0040601820000000L});
	public static final BitSet FOLLOW_compOp_in_comparisonPredicate2825 = new BitSet(new long[]{0x0000000180080800L,0x0000000000600100L});
	public static final BitSet FOLLOW_literalOrParameterName_in_comparisonPredicate2827 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_literalOrParameterName3007 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parameterName_in_literalOrParameterName3022 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_signedNumericLiteral_in_literal3055 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_characterStringLiteral_in_literal3067 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_booleanLiteral_in_literal3079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_datetimeLiteral_in_literal3091 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_columnReference_in_inPredicate3124 = new BitSet(new long[]{0x0020004000000000L});
	public static final BitSet FOLLOW_NOT_in_inPredicate3126 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_IN_in_inPredicate3129 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAREN_in_inPredicate3131 = new BitSet(new long[]{0x0000000180080800L,0x0000000000600100L});
	public static final BitSet FOLLOW_inValueList_in_inPredicate3133 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_inPredicate3135 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literalOrParameterName_in_inValueList3223 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_COMMA_in_inValueList3226 = new BitSet(new long[]{0x0000000180080800L,0x0000000000600100L});
	public static final BitSet FOLLOW_literalOrParameterName_in_inValueList3228 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_columnReference_in_likePredicate3312 = new BitSet(new long[]{0x0020800000000000L});
	public static final BitSet FOLLOW_NOT_in_likePredicate3314 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_LIKE_in_likePredicate3317 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_characterStringLiteral_in_likePredicate3319 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_columnReference_in_nullPredicate3405 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_IS_in_nullPredicate3407 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_NULL_in_nullPredicate3409 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_columnReference_in_nullPredicate3471 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_IS_in_nullPredicate3473 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_NOT_in_nullPredicate3475 = new BitSet(new long[]{0x0080000000000000L});
	public static final BitSet FOLLOW_NULL_in_nullPredicate3477 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literalOrParameterName_in_quantifiedComparisonPredicate3558 = new BitSet(new long[]{0x0040601820000000L});
	public static final BitSet FOLLOW_compOp_in_quantifiedComparisonPredicate3560 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_ANY_in_quantifiedComparisonPredicate3562 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnReference_in_quantifiedComparisonPredicate3564 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ANY_in_quantifiedInPredicate3651 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_columnReference_in_quantifiedInPredicate3653 = new BitSet(new long[]{0x0020004000000000L});
	public static final BitSet FOLLOW_NOT_in_quantifiedInPredicate3655 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_IN_in_quantifiedInPredicate3658 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAREN_in_quantifiedInPredicate3660 = new BitSet(new long[]{0x0000000180080800L,0x0000000000600100L});
	public static final BitSet FOLLOW_inValueList_in_quantifiedInPredicate3662 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_quantifiedInPredicate3664 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONTAINS_in_textSearchPredicate3752 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAREN_in_textSearchPredicate3754 = new BitSet(new long[]{0x0000002008000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_qualifier_in_textSearchPredicate3757 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_COMMA_in_textSearchPredicate3759 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_textSearchExpression_in_textSearchPredicate3763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_textSearchPredicate3765 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IN_FOLDER_in_folderPredicate3849 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_folderPredicateArgs_in_folderPredicate3851 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IN_TREE_in_folderPredicate3911 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_folderPredicateArgs_in_folderPredicate3913 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_folderPredicateArgs3994 = new BitSet(new long[]{0x0000002008000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_qualifier_in_folderPredicateArgs3997 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_COMMA_in_folderPredicateArgs3999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_folderId_in_folderPredicateArgs4003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_folderPredicateArgs4005 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ORDER_in_orderByClause4071 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_BY_in_orderByClause4073 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_sortSpecification_in_orderByClause4075 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_COMMA_in_orderByClause4078 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_sortSpecification_in_orderByClause4080 = new BitSet(new long[]{0x0000000000008002L});
	public static final BitSet FOLLOW_columnReference_in_sortSpecification4164 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_columnReference_in_sortSpecification4226 = new BitSet(new long[]{0x0000000000200100L});
	public static final BitSet FOLLOW_ASC_in_sortSpecification4256 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DESC_in_sortSpecification4278 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_correlationName4372 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_tableName4408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_columnName4461 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COLON_in_parameterName4514 = new BitSet(new long[]{0x0000002008000000L});
	public static final BitSet FOLLOW_identifier_in_parameterName4516 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_characterStringLiteral_in_folderId4597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUOTED_STRING_in_textSearchExpression4650 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_identifier4683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_QUOTE_in_identifier4718 = new BitSet(new long[]{0x1EA09FE2802205E0L,0x0000000000E00C00L});
	public static final BitSet FOLLOW_keyWordOrId_in_identifier4720 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_DOUBLE_QUOTE_in_identifier4722 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOATING_POINT_LITERAL_in_signedNumericLiteral4801 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_integerLiteral_in_signedNumericLiteral4861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_integerLiteral4914 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRUE_in_booleanLiteral4995 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FALSE_in_booleanLiteral5055 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TIMESTAMP_in_datetimeLiteral5136 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_QUOTED_STRING_in_datetimeLiteral5138 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUOTED_STRING_in_characterStringLiteral5219 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SELECT_in_keyWord5300 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AS_in_keyWord5312 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FROM_in_keyWord5324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_JOIN_in_keyWord5336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INNER_in_keyWord5348 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_in_keyWord5360 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OUTER_in_keyWord5372 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ON_in_keyWord5384 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WHERE_in_keyWord5396 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_keyWord5408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_keyWord5420 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_keyWord5432 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IN_in_keyWord5444 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LIKE_in_keyWord5456 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IS_in_keyWord5468 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_keyWord5480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ANY_in_keyWord5492 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONTAINS_in_keyWord5504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IN_FOLDER_in_keyWord5516 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IN_TREE_in_keyWord5528 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ORDER_in_keyWord5540 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BY_in_keyWord5552 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASC_in_keyWord5564 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DESC_in_keyWord5576 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TIMESTAMP_in_keyWord5588 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRUE_in_keyWord5600 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FALSE_in_keyWord5612 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisFunction_in_keyWord5624 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SCORE_in_cmisFunction5657 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_keyWord_in_keyWordOrId5710 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_keyWordOrId5742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tableName_in_synpred1_CMIS1324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_synpred2_CMIS1918 = new BitSet(new long[]{0x0002002008000000L});
	public static final BitSet FOLLOW_singleTable_in_synpred2_CMIS1920 = new BitSet(new long[]{0x0000188000000000L});
	public static final BitSet FOLLOW_joinedTable_in_synpred2_CMIS1922 = new BitSet(new long[]{0x0000188000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_synpred2_CMIS1925 = new BitSet(new long[]{0x0000000000000002L});
}
