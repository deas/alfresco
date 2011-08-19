// $ANTLR 3.3 Nov 30, 2010 12:50:56 W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2011-08-18 15:13:18

package org.alfresco.repo.search.impl.parsers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class FTSParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "FTS", "DISJUNCTION", "CONJUNCTION", "NEGATION", "TERM", "EXACT_TERM", "PHRASE", "EXACT_PHRASE", "SYNONYM", "RANGE", "PROXIMITY", "DEFAULT", "MANDATORY", "OPTIONAL", "EXCLUDE", "FIELD_DISJUNCTION", "FIELD_CONJUNCTION", "FIELD_NEGATION", "FIELD_GROUP", "FIELD_DEFAULT", "FIELD_MANDATORY", "FIELD_OPTIONAL", "FIELD_EXCLUDE", "FG_TERM", "FG_EXACT_TERM", "FG_PHRASE", "FG_EXACT_PHRASE", "FG_SYNONYM", "FG_PROXIMITY", "FG_RANGE", "FIELD_REF", "INCLUSIVE", "EXCLUSIVE", "QUALIFIER", "PREFIX", "NAME_SPACE", "BOOST", "FUZZY", "TEMPLATE", "PLUS", "BAR", "MINUS", "LPAREN", "RPAREN", "PERCENT", "COMMA", "TILDA", "DECIMAL_INTEGER_LITERAL", "CARAT", "COLON", "EQUALS", "FTSPHRASE", "ID", "FTSWORD", "FTSPRE", "FTSWILD", "NOT", "TO", "FLOATING_POINT_LITERAL", "STAR", "DOTDOT", "LSQUARE", "LT", "RSQUARE", "GT", "AT", "URI", "DOT", "QUESTION_MARK", "OR", "AND", "AMP", "EXCLAMATION", "F_ESC", "F_URI_ALPHA", "F_URI_DIGIT", "F_URI_OTHER", "F_HEX", "F_URI_ESC", "LCURL", "RCURL", "DOLLAR", "DECIMAL_NUMERAL", "INWORD", "START_RANGE_I", "START_RANGE_F", "DIGIT", "EXPONENT", "ZERO_DIGIT", "NON_ZERO_DIGIT", "E", "SIGNED_INTEGER", "WS"
    };
    public static final int EOF=-1;
    public static final int FTS=4;
    public static final int DISJUNCTION=5;
    public static final int CONJUNCTION=6;
    public static final int NEGATION=7;
    public static final int TERM=8;
    public static final int EXACT_TERM=9;
    public static final int PHRASE=10;
    public static final int EXACT_PHRASE=11;
    public static final int SYNONYM=12;
    public static final int RANGE=13;
    public static final int PROXIMITY=14;
    public static final int DEFAULT=15;
    public static final int MANDATORY=16;
    public static final int OPTIONAL=17;
    public static final int EXCLUDE=18;
    public static final int FIELD_DISJUNCTION=19;
    public static final int FIELD_CONJUNCTION=20;
    public static final int FIELD_NEGATION=21;
    public static final int FIELD_GROUP=22;
    public static final int FIELD_DEFAULT=23;
    public static final int FIELD_MANDATORY=24;
    public static final int FIELD_OPTIONAL=25;
    public static final int FIELD_EXCLUDE=26;
    public static final int FG_TERM=27;
    public static final int FG_EXACT_TERM=28;
    public static final int FG_PHRASE=29;
    public static final int FG_EXACT_PHRASE=30;
    public static final int FG_SYNONYM=31;
    public static final int FG_PROXIMITY=32;
    public static final int FG_RANGE=33;
    public static final int FIELD_REF=34;
    public static final int INCLUSIVE=35;
    public static final int EXCLUSIVE=36;
    public static final int QUALIFIER=37;
    public static final int PREFIX=38;
    public static final int NAME_SPACE=39;
    public static final int BOOST=40;
    public static final int FUZZY=41;
    public static final int TEMPLATE=42;
    public static final int PLUS=43;
    public static final int BAR=44;
    public static final int MINUS=45;
    public static final int LPAREN=46;
    public static final int RPAREN=47;
    public static final int PERCENT=48;
    public static final int COMMA=49;
    public static final int TILDA=50;
    public static final int DECIMAL_INTEGER_LITERAL=51;
    public static final int CARAT=52;
    public static final int COLON=53;
    public static final int EQUALS=54;
    public static final int FTSPHRASE=55;
    public static final int ID=56;
    public static final int FTSWORD=57;
    public static final int FTSPRE=58;
    public static final int FTSWILD=59;
    public static final int NOT=60;
    public static final int TO=61;
    public static final int FLOATING_POINT_LITERAL=62;
    public static final int STAR=63;
    public static final int DOTDOT=64;
    public static final int LSQUARE=65;
    public static final int LT=66;
    public static final int RSQUARE=67;
    public static final int GT=68;
    public static final int AT=69;
    public static final int URI=70;
    public static final int DOT=71;
    public static final int QUESTION_MARK=72;
    public static final int OR=73;
    public static final int AND=74;
    public static final int AMP=75;
    public static final int EXCLAMATION=76;
    public static final int F_ESC=77;
    public static final int F_URI_ALPHA=78;
    public static final int F_URI_DIGIT=79;
    public static final int F_URI_OTHER=80;
    public static final int F_HEX=81;
    public static final int F_URI_ESC=82;
    public static final int LCURL=83;
    public static final int RCURL=84;
    public static final int DOLLAR=85;
    public static final int DECIMAL_NUMERAL=86;
    public static final int INWORD=87;
    public static final int START_RANGE_I=88;
    public static final int START_RANGE_F=89;
    public static final int DIGIT=90;
    public static final int EXPONENT=91;
    public static final int ZERO_DIGIT=92;
    public static final int NON_ZERO_DIGIT=93;
    public static final int E=94;
    public static final int SIGNED_INTEGER=95;
    public static final int WS=96;

    // delegates
    // delegators


        public FTSParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public FTSParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return FTSParser.tokenNames; }
    public String getGrammarFileName() { return "W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g"; }


        public enum Mode
        {
            CMIS, DEFAULT_CONJUNCTION, DEFAULT_DISJUNCTION
        }

        private Stack<String> paraphrases = new Stack<String>();
        
        private boolean defaultFieldConjunction = true;
        
        private Mode mode = Mode.DEFAULT_CONJUNCTION;
        
        public Mode getMode()
        {
           return mode;
        }
        
        public void setMode(Mode mode)
        {
           this.mode = mode;
        }
        
        public boolean defaultFieldConjunction()
        {
           return defaultFieldConjunction;
        }
        
        public void setDefaultFieldConjunction(boolean defaultFieldConjunction)
        {
           this.defaultFieldConjunction = defaultFieldConjunction;
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


    public static class ftsQuery_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsQuery"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:351:1: ftsQuery : ftsDisjunction EOF -> ftsDisjunction ;
    public final FTSParser.ftsQuery_return ftsQuery() throws RecognitionException {
        FTSParser.ftsQuery_return retval = new FTSParser.ftsQuery_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        FTSParser.ftsDisjunction_return ftsDisjunction1 = null;


        Object EOF2_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_ftsDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsDisjunction");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:352:9: ( ftsDisjunction EOF -> ftsDisjunction )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:353:9: ftsDisjunction EOF
            {
            pushFollow(FOLLOW_ftsDisjunction_in_ftsQuery559);
            ftsDisjunction1=ftsDisjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsDisjunction.add(ftsDisjunction1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_ftsQuery561); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF2);



            // AST REWRITE
            // elements: ftsDisjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 354:17: -> ftsDisjunction
            {
                adaptor.addChild(root_0, stream_ftsDisjunction.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsQuery"

    public static class ftsDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:362:1: ftsDisjunction : ({...}? => cmisExplicitDisjunction | {...}? => ftsExplicitDisjunction | {...}? => ftsImplicitDisjunction );
    public final FTSParser.ftsDisjunction_return ftsDisjunction() throws RecognitionException {
        FTSParser.ftsDisjunction_return retval = new FTSParser.ftsDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.cmisExplicitDisjunction_return cmisExplicitDisjunction3 = null;

        FTSParser.ftsExplicitDisjunction_return ftsExplicitDisjunction4 = null;

        FTSParser.ftsImplicitDisjunction_return ftsImplicitDisjunction5 = null;



        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:363:9: ({...}? => cmisExplicitDisjunction | {...}? => ftsExplicitDisjunction | {...}? => ftsImplicitDisjunction )
            int alt1=3;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:364:9: {...}? => cmisExplicitDisjunction
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((getMode() == Mode.CMIS)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.CMIS");
                    }
                    pushFollow(FOLLOW_cmisExplicitDisjunction_in_ftsDisjunction620);
                    cmisExplicitDisjunction3=cmisExplicitDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cmisExplicitDisjunction3.getTree());

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:365:11: {...}? => ftsExplicitDisjunction
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.DEFAULT_CONJUNCTION");
                    }
                    pushFollow(FOLLOW_ftsExplicitDisjunction_in_ftsDisjunction635);
                    ftsExplicitDisjunction4=ftsExplicitDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsExplicitDisjunction4.getTree());

                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:366:11: {...}? => ftsImplicitDisjunction
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.DEFAULT_DISJUNCTION");
                    }
                    pushFollow(FOLLOW_ftsImplicitDisjunction_in_ftsDisjunction650);
                    ftsImplicitDisjunction5=ftsImplicitDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsImplicitDisjunction5.getTree());

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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsDisjunction"

    public static class ftsExplicitDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsExplicitDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:369:1: ftsExplicitDisjunction : ftsImplicitConjunction ( or ftsImplicitConjunction )* -> ^( DISJUNCTION ( ftsImplicitConjunction )+ ) ;
    public final FTSParser.ftsExplicitDisjunction_return ftsExplicitDisjunction() throws RecognitionException {
        FTSParser.ftsExplicitDisjunction_return retval = new FTSParser.ftsExplicitDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsImplicitConjunction_return ftsImplicitConjunction6 = null;

        FTSParser.or_return or7 = null;

        FTSParser.ftsImplicitConjunction_return ftsImplicitConjunction8 = null;


        RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
        RewriteRuleSubtreeStream stream_ftsImplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsImplicitConjunction");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:370:9: ( ftsImplicitConjunction ( or ftsImplicitConjunction )* -> ^( DISJUNCTION ( ftsImplicitConjunction )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:371:9: ftsImplicitConjunction ( or ftsImplicitConjunction )*
            {
            pushFollow(FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction683);
            ftsImplicitConjunction6=ftsImplicitConjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsImplicitConjunction.add(ftsImplicitConjunction6.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:371:32: ( or ftsImplicitConjunction )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==BAR||LA2_0==OR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:371:33: or ftsImplicitConjunction
            	    {
            	    pushFollow(FOLLOW_or_in_ftsExplicitDisjunction686);
            	    or7=or();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_or.add(or7.getTree());
            	    pushFollow(FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction688);
            	    ftsImplicitConjunction8=ftsImplicitConjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsImplicitConjunction.add(ftsImplicitConjunction8.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);



            // AST REWRITE
            // elements: ftsImplicitConjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 372:17: -> ^( DISJUNCTION ( ftsImplicitConjunction )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:373:25: ^( DISJUNCTION ( ftsImplicitConjunction )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);

                if ( !(stream_ftsImplicitConjunction.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsImplicitConjunction.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsImplicitConjunction.nextTree());

                }
                stream_ftsImplicitConjunction.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsExplicitDisjunction"

    public static class cmisExplicitDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisExplicitDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:1: cmisExplicitDisjunction : cmisConjunction ( or cmisConjunction )* -> ^( DISJUNCTION ( cmisConjunction )+ ) ;
    public final FTSParser.cmisExplicitDisjunction_return cmisExplicitDisjunction() throws RecognitionException {
        FTSParser.cmisExplicitDisjunction_return retval = new FTSParser.cmisExplicitDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.cmisConjunction_return cmisConjunction9 = null;

        FTSParser.or_return or10 = null;

        FTSParser.cmisConjunction_return cmisConjunction11 = null;


        RewriteRuleSubtreeStream stream_cmisConjunction=new RewriteRuleSubtreeStream(adaptor,"rule cmisConjunction");
        RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:377:9: ( cmisConjunction ( or cmisConjunction )* -> ^( DISJUNCTION ( cmisConjunction )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:378:9: cmisConjunction ( or cmisConjunction )*
            {
            pushFollow(FOLLOW_cmisConjunction_in_cmisExplicitDisjunction772);
            cmisConjunction9=cmisConjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cmisConjunction.add(cmisConjunction9.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:378:25: ( or cmisConjunction )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==BAR||LA3_0==OR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:378:26: or cmisConjunction
            	    {
            	    pushFollow(FOLLOW_or_in_cmisExplicitDisjunction775);
            	    or10=or();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_or.add(or10.getTree());
            	    pushFollow(FOLLOW_cmisConjunction_in_cmisExplicitDisjunction777);
            	    cmisConjunction11=cmisConjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_cmisConjunction.add(cmisConjunction11.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);



            // AST REWRITE
            // elements: cmisConjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 379:17: -> ^( DISJUNCTION ( cmisConjunction )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:380:25: ^( DISJUNCTION ( cmisConjunction )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);

                if ( !(stream_cmisConjunction.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_cmisConjunction.hasNext() ) {
                    adaptor.addChild(root_1, stream_cmisConjunction.nextTree());

                }
                stream_cmisConjunction.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisExplicitDisjunction"

    public static class ftsImplicitDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsImplicitDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:383:1: ftsImplicitDisjunction : ( ( or )? ftsExplicitConjunction )+ -> ^( DISJUNCTION ( ftsExplicitConjunction )+ ) ;
    public final FTSParser.ftsImplicitDisjunction_return ftsImplicitDisjunction() throws RecognitionException {
        FTSParser.ftsImplicitDisjunction_return retval = new FTSParser.ftsImplicitDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.or_return or12 = null;

        FTSParser.ftsExplicitConjunction_return ftsExplicitConjunction13 = null;


        RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
        RewriteRuleSubtreeStream stream_ftsExplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsExplicitConjunction");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:384:9: ( ( ( or )? ftsExplicitConjunction )+ -> ^( DISJUNCTION ( ftsExplicitConjunction )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:9: ( ( or )? ftsExplicitConjunction )+
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:9: ( ( or )? ftsExplicitConjunction )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                alt5 = dfa5.predict(input);
                switch (alt5) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:10: ( or )? ftsExplicitConjunction
            	    {
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:10: ( or )?
            	    int alt4=2;
            	    alt4 = dfa4.predict(input);
            	    switch (alt4) {
            	        case 1 :
            	            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:10: or
            	            {
            	            pushFollow(FOLLOW_or_in_ftsImplicitDisjunction862);
            	            or12=or();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_or.add(or12.getTree());

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_ftsExplicitConjunction_in_ftsImplicitDisjunction865);
            	    ftsExplicitConjunction13=ftsExplicitConjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsExplicitConjunction.add(ftsExplicitConjunction13.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);



            // AST REWRITE
            // elements: ftsExplicitConjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 386:17: -> ^( DISJUNCTION ( ftsExplicitConjunction )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:387:25: ^( DISJUNCTION ( ftsExplicitConjunction )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);

                if ( !(stream_ftsExplicitConjunction.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsExplicitConjunction.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsExplicitConjunction.nextTree());

                }
                stream_ftsExplicitConjunction.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsImplicitDisjunction"

    public static class ftsExplicitConjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsExplicitConjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:1: ftsExplicitConjunction : ftsPrefixed ( and ftsPrefixed )* -> ^( CONJUNCTION ( ftsPrefixed )+ ) ;
    public final FTSParser.ftsExplicitConjunction_return ftsExplicitConjunction() throws RecognitionException {
        FTSParser.ftsExplicitConjunction_return retval = new FTSParser.ftsExplicitConjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsPrefixed_return ftsPrefixed14 = null;

        FTSParser.and_return and15 = null;

        FTSParser.ftsPrefixed_return ftsPrefixed16 = null;


        RewriteRuleSubtreeStream stream_ftsPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsPrefixed");
        RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:395:9: ( ftsPrefixed ( and ftsPrefixed )* -> ^( CONJUNCTION ( ftsPrefixed )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:396:9: ftsPrefixed ( and ftsPrefixed )*
            {
            pushFollow(FOLLOW_ftsPrefixed_in_ftsExplicitConjunction952);
            ftsPrefixed14=ftsPrefixed();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed14.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:396:21: ( and ftsPrefixed )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>=AND && LA6_0<=AMP)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:396:22: and ftsPrefixed
            	    {
            	    pushFollow(FOLLOW_and_in_ftsExplicitConjunction955);
            	    and15=and();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_and.add(and15.getTree());
            	    pushFollow(FOLLOW_ftsPrefixed_in_ftsExplicitConjunction957);
            	    ftsPrefixed16=ftsPrefixed();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed16.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);



            // AST REWRITE
            // elements: ftsPrefixed
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 397:17: -> ^( CONJUNCTION ( ftsPrefixed )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:398:25: ^( CONJUNCTION ( ftsPrefixed )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);

                if ( !(stream_ftsPrefixed.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsPrefixed.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsPrefixed.nextTree());

                }
                stream_ftsPrefixed.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsExplicitConjunction"

    public static class ftsImplicitConjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsImplicitConjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:401:1: ftsImplicitConjunction : ( ( and )? ftsPrefixed )+ -> ^( CONJUNCTION ( ftsPrefixed )+ ) ;
    public final FTSParser.ftsImplicitConjunction_return ftsImplicitConjunction() throws RecognitionException {
        FTSParser.ftsImplicitConjunction_return retval = new FTSParser.ftsImplicitConjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.and_return and17 = null;

        FTSParser.ftsPrefixed_return ftsPrefixed18 = null;


        RewriteRuleSubtreeStream stream_ftsPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsPrefixed");
        RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:402:9: ( ( ( and )? ftsPrefixed )+ -> ^( CONJUNCTION ( ftsPrefixed )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:9: ( ( and )? ftsPrefixed )+
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:9: ( ( and )? ftsPrefixed )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                alt8 = dfa8.predict(input);
                switch (alt8) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:10: ( and )? ftsPrefixed
            	    {
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:10: ( and )?
            	    int alt7=2;
            	    alt7 = dfa7.predict(input);
            	    switch (alt7) {
            	        case 1 :
            	            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:10: and
            	            {
            	            pushFollow(FOLLOW_and_in_ftsImplicitConjunction1042);
            	            and17=and();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_and.add(and17.getTree());

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_ftsPrefixed_in_ftsImplicitConjunction1045);
            	    ftsPrefixed18=ftsPrefixed();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed18.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);



            // AST REWRITE
            // elements: ftsPrefixed
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 404:17: -> ^( CONJUNCTION ( ftsPrefixed )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:405:25: ^( CONJUNCTION ( ftsPrefixed )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);

                if ( !(stream_ftsPrefixed.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsPrefixed.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsPrefixed.nextTree());

                }
                stream_ftsPrefixed.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsImplicitConjunction"

    public static class cmisConjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisConjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:408:1: cmisConjunction : ( cmisPrefixed )+ -> ^( CONJUNCTION ( cmisPrefixed )+ ) ;
    public final FTSParser.cmisConjunction_return cmisConjunction() throws RecognitionException {
        FTSParser.cmisConjunction_return retval = new FTSParser.cmisConjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.cmisPrefixed_return cmisPrefixed19 = null;


        RewriteRuleSubtreeStream stream_cmisPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule cmisPrefixed");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:409:9: ( ( cmisPrefixed )+ -> ^( CONJUNCTION ( cmisPrefixed )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:410:9: ( cmisPrefixed )+
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:410:9: ( cmisPrefixed )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==MINUS||LA9_0==DECIMAL_INTEGER_LITERAL||(LA9_0>=FTSPHRASE && LA9_0<=STAR)||LA9_0==QUESTION_MARK) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:410:9: cmisPrefixed
            	    {
            	    pushFollow(FOLLOW_cmisPrefixed_in_cmisConjunction1129);
            	    cmisPrefixed19=cmisPrefixed();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_cmisPrefixed.add(cmisPrefixed19.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);



            // AST REWRITE
            // elements: cmisPrefixed
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 411:17: -> ^( CONJUNCTION ( cmisPrefixed )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:412:25: ^( CONJUNCTION ( cmisPrefixed )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);

                if ( !(stream_cmisPrefixed.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_cmisPrefixed.hasNext() ) {
                    adaptor.addChild(root_1, stream_cmisPrefixed.nextTree());

                }
                stream_cmisPrefixed.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisConjunction"

    public static class ftsPrefixed_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsPrefixed"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:422:1: ftsPrefixed : ( ( not )=> not ftsTest ( boost )? -> ^( NEGATION ftsTest ( boost )? ) | ftsTest ( boost )? -> ^( DEFAULT ftsTest ( boost )? ) | PLUS ftsTest ( boost )? -> ^( MANDATORY ftsTest ( boost )? ) | BAR ftsTest ( boost )? -> ^( OPTIONAL ftsTest ( boost )? ) | MINUS ftsTest ( boost )? -> ^( EXCLUDE ftsTest ( boost )? ) );
    public final FTSParser.ftsPrefixed_return ftsPrefixed() throws RecognitionException {
        FTSParser.ftsPrefixed_return retval = new FTSParser.ftsPrefixed_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS25=null;
        Token BAR28=null;
        Token MINUS31=null;
        FTSParser.not_return not20 = null;

        FTSParser.ftsTest_return ftsTest21 = null;

        FTSParser.boost_return boost22 = null;

        FTSParser.ftsTest_return ftsTest23 = null;

        FTSParser.boost_return boost24 = null;

        FTSParser.ftsTest_return ftsTest26 = null;

        FTSParser.boost_return boost27 = null;

        FTSParser.ftsTest_return ftsTest29 = null;

        FTSParser.boost_return boost30 = null;

        FTSParser.ftsTest_return ftsTest32 = null;

        FTSParser.boost_return boost33 = null;


        Object PLUS25_tree=null;
        Object BAR28_tree=null;
        Object MINUS31_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_not=new RewriteRuleSubtreeStream(adaptor,"rule not");
        RewriteRuleSubtreeStream stream_ftsTest=new RewriteRuleSubtreeStream(adaptor,"rule ftsTest");
        RewriteRuleSubtreeStream stream_boost=new RewriteRuleSubtreeStream(adaptor,"rule boost");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:423:9: ( ( not )=> not ftsTest ( boost )? -> ^( NEGATION ftsTest ( boost )? ) | ftsTest ( boost )? -> ^( DEFAULT ftsTest ( boost )? ) | PLUS ftsTest ( boost )? -> ^( MANDATORY ftsTest ( boost )? ) | BAR ftsTest ( boost )? -> ^( OPTIONAL ftsTest ( boost )? ) | MINUS ftsTest ( boost )? -> ^( EXCLUDE ftsTest ( boost )? ) )
            int alt15=5;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:9: ( not )=> not ftsTest ( boost )?
                    {
                    pushFollow(FOLLOW_not_in_ftsPrefixed1221);
                    not20=not();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_not.add(not20.getTree());
                    pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1223);
                    ftsTest21=ftsTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest21.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:30: ( boost )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==CARAT) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:30: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsPrefixed1225);
                            boost22=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost22.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 425:17: -> ^( NEGATION ftsTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:426:25: ^( NEGATION ftsTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NEGATION, "NEGATION"), root_1);

                        adaptor.addChild(root_1, stream_ftsTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:426:44: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:11: ftsTest ( boost )?
                    {
                    pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1289);
                    ftsTest23=ftsTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest23.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:19: ( boost )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==CARAT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:19: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsPrefixed1291);
                            boost24=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost24.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 428:17: -> ^( DEFAULT ftsTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:429:25: ^( DEFAULT ftsTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DEFAULT, "DEFAULT"), root_1);

                        adaptor.addChild(root_1, stream_ftsTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:429:43: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:430:11: PLUS ftsTest ( boost )?
                    {
                    PLUS25=(Token)match(input,PLUS,FOLLOW_PLUS_in_ftsPrefixed1355); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS25);

                    pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1357);
                    ftsTest26=ftsTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest26.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:430:24: ( boost )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==CARAT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:430:24: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsPrefixed1359);
                            boost27=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost27.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 431:17: -> ^( MANDATORY ftsTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:432:25: ^( MANDATORY ftsTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(MANDATORY, "MANDATORY"), root_1);

                        adaptor.addChild(root_1, stream_ftsTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:432:45: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:433:11: BAR ftsTest ( boost )?
                    {
                    BAR28=(Token)match(input,BAR,FOLLOW_BAR_in_ftsPrefixed1423); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BAR.add(BAR28);

                    pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1425);
                    ftsTest29=ftsTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest29.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:433:23: ( boost )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==CARAT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:433:23: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsPrefixed1427);
                            boost30=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost30.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 434:17: -> ^( OPTIONAL ftsTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:435:25: ^( OPTIONAL ftsTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONAL, "OPTIONAL"), root_1);

                        adaptor.addChild(root_1, stream_ftsTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:435:44: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:436:11: MINUS ftsTest ( boost )?
                    {
                    MINUS31=(Token)match(input,MINUS,FOLLOW_MINUS_in_ftsPrefixed1491); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS.add(MINUS31);

                    pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1493);
                    ftsTest32=ftsTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest32.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:436:25: ( boost )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==CARAT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:436:25: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsPrefixed1495);
                            boost33=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost33.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 437:17: -> ^( EXCLUDE ftsTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:438:25: ^( EXCLUDE ftsTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXCLUDE, "EXCLUDE"), root_1);

                        adaptor.addChild(root_1, stream_ftsTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:438:43: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsPrefixed"

    public static class cmisPrefixed_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisPrefixed"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:441:1: cmisPrefixed : ( cmisTest -> ^( DEFAULT cmisTest ) | MINUS cmisTest -> ^( EXCLUDE cmisTest ) );
    public final FTSParser.cmisPrefixed_return cmisPrefixed() throws RecognitionException {
        FTSParser.cmisPrefixed_return retval = new FTSParser.cmisPrefixed_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS35=null;
        FTSParser.cmisTest_return cmisTest34 = null;

        FTSParser.cmisTest_return cmisTest36 = null;


        Object MINUS35_tree=null;
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleSubtreeStream stream_cmisTest=new RewriteRuleSubtreeStream(adaptor,"rule cmisTest");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:442:9: ( cmisTest -> ^( DEFAULT cmisTest ) | MINUS cmisTest -> ^( EXCLUDE cmisTest ) )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==DECIMAL_INTEGER_LITERAL||(LA16_0>=FTSPHRASE && LA16_0<=STAR)||LA16_0==QUESTION_MARK) ) {
                alt16=1;
            }
            else if ( (LA16_0==MINUS) ) {
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
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:443:9: cmisTest
                    {
                    pushFollow(FOLLOW_cmisTest_in_cmisPrefixed1580);
                    cmisTest34=cmisTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cmisTest.add(cmisTest34.getTree());


                    // AST REWRITE
                    // elements: cmisTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 444:17: -> ^( DEFAULT cmisTest )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:445:25: ^( DEFAULT cmisTest )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DEFAULT, "DEFAULT"), root_1);

                        adaptor.addChild(root_1, stream_cmisTest.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:446:11: MINUS cmisTest
                    {
                    MINUS35=(Token)match(input,MINUS,FOLLOW_MINUS_in_cmisPrefixed1640); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS.add(MINUS35);

                    pushFollow(FOLLOW_cmisTest_in_cmisPrefixed1642);
                    cmisTest36=cmisTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cmisTest.add(cmisTest36.getTree());


                    // AST REWRITE
                    // elements: cmisTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 447:17: -> ^( EXCLUDE cmisTest )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:448:25: ^( EXCLUDE cmisTest )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXCLUDE, "EXCLUDE"), root_1);

                        adaptor.addChild(root_1, stream_cmisTest.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisPrefixed"

    public static class ftsTest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsTest"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:455:1: ftsTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ftsTerm ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsTerm ( fuzzy )? ) | ftsExactTerm ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsExactTerm ( fuzzy )? ) | ftsPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsPhrase ( slop )? ) | ftsExactPhrase ( ( slop )=> slop )? -> ^( EXACT_PHRASE ftsExactPhrase ( slop )? ) | ftsTokenisedPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsTokenisedPhrase ( slop )? ) | ftsSynonym ( ( fuzzy )=> fuzzy )? -> ^( SYNONYM ftsSynonym ( fuzzy )? ) | ftsRange -> ^( RANGE ftsRange ) | ftsFieldGroup -> ftsFieldGroup | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template );
    public final FTSParser.ftsTest_return ftsTest() throws RecognitionException {
        FTSParser.ftsTest_return retval = new FTSParser.ftsTest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LPAREN52=null;
        Token RPAREN54=null;
        FTSParser.ftsFieldGroupProximity_return ftsFieldGroupProximity37 = null;

        FTSParser.ftsTerm_return ftsTerm38 = null;

        FTSParser.fuzzy_return fuzzy39 = null;

        FTSParser.ftsExactTerm_return ftsExactTerm40 = null;

        FTSParser.fuzzy_return fuzzy41 = null;

        FTSParser.ftsPhrase_return ftsPhrase42 = null;

        FTSParser.slop_return slop43 = null;

        FTSParser.ftsExactPhrase_return ftsExactPhrase44 = null;

        FTSParser.slop_return slop45 = null;

        FTSParser.ftsTokenisedPhrase_return ftsTokenisedPhrase46 = null;

        FTSParser.slop_return slop47 = null;

        FTSParser.ftsSynonym_return ftsSynonym48 = null;

        FTSParser.fuzzy_return fuzzy49 = null;

        FTSParser.ftsRange_return ftsRange50 = null;

        FTSParser.ftsFieldGroup_return ftsFieldGroup51 = null;

        FTSParser.ftsDisjunction_return ftsDisjunction53 = null;

        FTSParser.template_return template55 = null;


        Object LPAREN52_tree=null;
        Object RPAREN54_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_ftsFieldGroup=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroup");
        RewriteRuleSubtreeStream stream_template=new RewriteRuleSubtreeStream(adaptor,"rule template");
        RewriteRuleSubtreeStream stream_ftsTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsTerm");
        RewriteRuleSubtreeStream stream_ftsExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsExactPhrase");
        RewriteRuleSubtreeStream stream_ftsExactTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsExactTerm");
        RewriteRuleSubtreeStream stream_ftsDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsDisjunction");
        RewriteRuleSubtreeStream stream_ftsSynonym=new RewriteRuleSubtreeStream(adaptor,"rule ftsSynonym");
        RewriteRuleSubtreeStream stream_ftsRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsRange");
        RewriteRuleSubtreeStream stream_ftsFieldGroupProximity=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximity");
        RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
        RewriteRuleSubtreeStream stream_ftsPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsPhrase");
        RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
        RewriteRuleSubtreeStream stream_ftsTokenisedPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsTokenisedPhrase");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:456:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ftsTerm ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsTerm ( fuzzy )? ) | ftsExactTerm ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsExactTerm ( fuzzy )? ) | ftsPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsPhrase ( slop )? ) | ftsExactPhrase ( ( slop )=> slop )? -> ^( EXACT_PHRASE ftsExactPhrase ( slop )? ) | ftsTokenisedPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsTokenisedPhrase ( slop )? ) | ftsSynonym ( ( fuzzy )=> fuzzy )? -> ^( SYNONYM ftsSynonym ( fuzzy )? ) | ftsRange -> ^( RANGE ftsRange ) | ftsFieldGroup -> ftsFieldGroup | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template )
            int alt23=11;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:457:9: ( ftsFieldGroupProximity )=> ftsFieldGroupProximity
                    {
                    pushFollow(FOLLOW_ftsFieldGroupProximity_in_ftsTest1732);
                    ftsFieldGroupProximity37=ftsFieldGroupProximity();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupProximity.add(ftsFieldGroupProximity37.getTree());


                    // AST REWRITE
                    // elements: ftsFieldGroupProximity
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 458:17: -> ^( PROXIMITY ftsFieldGroupProximity )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:459:25: ^( PROXIMITY ftsFieldGroupProximity )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROXIMITY, "PROXIMITY"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupProximity.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:11: ftsTerm ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsTerm_in_ftsTest1792);
                    ftsTerm38=ftsTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTerm.add(ftsTerm38.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:19: ( ( fuzzy )=> fuzzy )?
                    int alt17=2;
                    alt17 = dfa17.predict(input);
                    switch (alt17) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:21: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsTest1802);
                            fuzzy39=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy39.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsTerm, fuzzy
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 461:17: -> ^( TERM ftsTerm ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:462:25: ^( TERM ftsTerm ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);

                        adaptor.addChild(root_1, stream_ftsTerm.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:462:40: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:11: ftsExactTerm ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsExactTerm_in_ftsTest1867);
                    ftsExactTerm40=ftsExactTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsExactTerm.add(ftsExactTerm40.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:24: ( ( fuzzy )=> fuzzy )?
                    int alt18=2;
                    alt18 = dfa18.predict(input);
                    switch (alt18) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:26: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsTest1877);
                            fuzzy41=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy41.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: fuzzy, ftsExactTerm
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 464:17: -> ^( EXACT_TERM ftsExactTerm ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:465:25: ^( EXACT_TERM ftsExactTerm ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_TERM, "EXACT_TERM"), root_1);

                        adaptor.addChild(root_1, stream_ftsExactTerm.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:465:51: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:466:11: ftsPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsPhrase_in_ftsTest1942);
                    ftsPhrase42=ftsPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsPhrase.add(ftsPhrase42.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:466:21: ( ( slop )=> slop )?
                    int alt19=2;
                    alt19 = dfa19.predict(input);
                    switch (alt19) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:466:23: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsTest1952);
                            slop43=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop43.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsPhrase, slop
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 467:17: -> ^( PHRASE ftsPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:468:25: ^( PHRASE ftsPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:468:44: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:11: ftsExactPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsExactPhrase_in_ftsTest2017);
                    ftsExactPhrase44=ftsExactPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsExactPhrase.add(ftsExactPhrase44.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:26: ( ( slop )=> slop )?
                    int alt20=2;
                    alt20 = dfa20.predict(input);
                    switch (alt20) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:28: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsTest2027);
                            slop45=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop45.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: slop, ftsExactPhrase
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 470:17: -> ^( EXACT_PHRASE ftsExactPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:471:25: ^( EXACT_PHRASE ftsExactPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_PHRASE, "EXACT_PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsExactPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:471:55: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:11: ftsTokenisedPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsTokenisedPhrase_in_ftsTest2092);
                    ftsTokenisedPhrase46=ftsTokenisedPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsTokenisedPhrase.add(ftsTokenisedPhrase46.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:30: ( ( slop )=> slop )?
                    int alt21=2;
                    alt21 = dfa21.predict(input);
                    switch (alt21) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:32: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsTest2102);
                            slop47=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop47.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsTokenisedPhrase, slop
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 473:17: -> ^( PHRASE ftsTokenisedPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:474:25: ^( PHRASE ftsTokenisedPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsTokenisedPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:474:53: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:11: ftsSynonym ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsSynonym_in_ftsTest2167);
                    ftsSynonym48=ftsSynonym();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsSynonym.add(ftsSynonym48.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:22: ( ( fuzzy )=> fuzzy )?
                    int alt22=2;
                    alt22 = dfa22.predict(input);
                    switch (alt22) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:24: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsTest2177);
                            fuzzy49=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy49.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsSynonym, fuzzy
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 476:17: -> ^( SYNONYM ftsSynonym ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:477:25: ^( SYNONYM ftsSynonym ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SYNONYM, "SYNONYM"), root_1);

                        adaptor.addChild(root_1, stream_ftsSynonym.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:477:46: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:478:11: ftsRange
                    {
                    pushFollow(FOLLOW_ftsRange_in_ftsTest2242);
                    ftsRange50=ftsRange();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsRange.add(ftsRange50.getTree());


                    // AST REWRITE
                    // elements: ftsRange
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 479:17: -> ^( RANGE ftsRange )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:480:25: ^( RANGE ftsRange )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_1);

                        adaptor.addChild(root_1, stream_ftsRange.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:481:11: ftsFieldGroup
                    {
                    pushFollow(FOLLOW_ftsFieldGroup_in_ftsTest2302);
                    ftsFieldGroup51=ftsFieldGroup();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroup.add(ftsFieldGroup51.getTree());


                    // AST REWRITE
                    // elements: ftsFieldGroup
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 482:17: -> ftsFieldGroup
                    {
                        adaptor.addChild(root_0, stream_ftsFieldGroup.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 10 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:483:11: LPAREN ftsDisjunction RPAREN
                    {
                    LPAREN52=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsTest2334); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN52);

                    pushFollow(FOLLOW_ftsDisjunction_in_ftsTest2336);
                    ftsDisjunction53=ftsDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsDisjunction.add(ftsDisjunction53.getTree());
                    RPAREN54=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsTest2338); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN54);



                    // AST REWRITE
                    // elements: ftsDisjunction
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 484:17: -> ftsDisjunction
                    {
                        adaptor.addChild(root_0, stream_ftsDisjunction.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 11 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:485:11: template
                    {
                    pushFollow(FOLLOW_template_in_ftsTest2370);
                    template55=template();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_template.add(template55.getTree());


                    // AST REWRITE
                    // elements: template
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 486:17: -> template
                    {
                        adaptor.addChild(root_0, stream_template.nextTree());

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsTest"

    public static class cmisTest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisTest"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:489:1: cmisTest : ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) );
    public final FTSParser.cmisTest_return cmisTest() throws RecognitionException {
        FTSParser.cmisTest_return retval = new FTSParser.cmisTest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.cmisTerm_return cmisTerm56 = null;

        FTSParser.cmisPhrase_return cmisPhrase57 = null;


        RewriteRuleSubtreeStream stream_cmisPhrase=new RewriteRuleSubtreeStream(adaptor,"rule cmisPhrase");
        RewriteRuleSubtreeStream stream_cmisTerm=new RewriteRuleSubtreeStream(adaptor,"rule cmisTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:490:9: ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==DECIMAL_INTEGER_LITERAL||(LA24_0>=ID && LA24_0<=STAR)||LA24_0==QUESTION_MARK) ) {
                alt24=1;
            }
            else if ( (LA24_0==FTSPHRASE) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:491:9: cmisTerm
                    {
                    pushFollow(FOLLOW_cmisTerm_in_cmisTest2423);
                    cmisTerm56=cmisTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cmisTerm.add(cmisTerm56.getTree());


                    // AST REWRITE
                    // elements: cmisTerm
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 492:17: -> ^( TERM cmisTerm )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:493:25: ^( TERM cmisTerm )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);

                        adaptor.addChild(root_1, stream_cmisTerm.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:494:11: cmisPhrase
                    {
                    pushFollow(FOLLOW_cmisPhrase_in_cmisTest2483);
                    cmisPhrase57=cmisPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cmisPhrase.add(cmisPhrase57.getTree());


                    // AST REWRITE
                    // elements: cmisPhrase
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 495:17: -> ^( PHRASE cmisPhrase )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:496:25: ^( PHRASE cmisPhrase )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_cmisPhrase.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisTest"

    public static class template_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "template"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:499:1: template : ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) );
    public final FTSParser.template_return template() throws RecognitionException {
        FTSParser.template_return retval = new FTSParser.template_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PERCENT58=null;
        Token PERCENT60=null;
        Token LPAREN61=null;
        Token COMMA63=null;
        Token RPAREN64=null;
        FTSParser.tempReference_return tempReference59 = null;

        FTSParser.tempReference_return tempReference62 = null;


        Object PERCENT58_tree=null;
        Object PERCENT60_tree=null;
        Object LPAREN61_tree=null;
        Object COMMA63_tree=null;
        Object RPAREN64_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_PERCENT=new RewriteRuleTokenStream(adaptor,"token PERCENT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_tempReference=new RewriteRuleSubtreeStream(adaptor,"rule tempReference");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:500:9: ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==PERCENT) ) {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    alt27=2;
                    }
                    break;
                case AT:
                    {
                    alt27=1;
                    }
                    break;
                case ID:
                    {
                    alt27=1;
                    }
                    break;
                case URI:
                    {
                    alt27=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 27, 1, input);

                    throw nvae;
                }

            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:501:9: PERCENT tempReference
                    {
                    PERCENT58=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2564); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT58);

                    pushFollow(FOLLOW_tempReference_in_template2566);
                    tempReference59=tempReference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tempReference.add(tempReference59.getTree());


                    // AST REWRITE
                    // elements: tempReference
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 502:17: -> ^( TEMPLATE tempReference )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:503:25: ^( TEMPLATE tempReference )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TEMPLATE, "TEMPLATE"), root_1);

                        adaptor.addChild(root_1, stream_tempReference.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:11: PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN
                    {
                    PERCENT60=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2626); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT60);

                    LPAREN61=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_template2628); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN61);

                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:26: ( tempReference ( COMMA )? )+
                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==ID||(LA26_0>=AT && LA26_0<=URI)) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:27: tempReference ( COMMA )?
                    	    {
                    	    pushFollow(FOLLOW_tempReference_in_template2631);
                    	    tempReference62=tempReference();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_tempReference.add(tempReference62.getTree());
                    	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:41: ( COMMA )?
                    	    int alt25=2;
                    	    int LA25_0 = input.LA(1);

                    	    if ( (LA25_0==COMMA) ) {
                    	        alt25=1;
                    	    }
                    	    switch (alt25) {
                    	        case 1 :
                    	            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:41: COMMA
                    	            {
                    	            COMMA63=(Token)match(input,COMMA,FOLLOW_COMMA_in_template2633); if (state.failed) return retval; 
                    	            if ( state.backtracking==0 ) stream_COMMA.add(COMMA63);


                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);

                    RPAREN64=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_template2638); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN64);



                    // AST REWRITE
                    // elements: tempReference
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 505:17: -> ^( TEMPLATE ( tempReference )+ )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:506:25: ^( TEMPLATE ( tempReference )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TEMPLATE, "TEMPLATE"), root_1);

                        if ( !(stream_tempReference.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_tempReference.hasNext() ) {
                            adaptor.addChild(root_1, stream_tempReference.nextTree());

                        }
                        stream_tempReference.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "template"

    public static class fuzzy_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fuzzy"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:509:1: fuzzy : TILDA number -> ^( FUZZY number ) ;
    public final FTSParser.fuzzy_return fuzzy() throws RecognitionException {
        FTSParser.fuzzy_return retval = new FTSParser.fuzzy_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA65=null;
        FTSParser.number_return number66 = null;


        Object TILDA65_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:510:9: ( TILDA number -> ^( FUZZY number ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:511:9: TILDA number
            {
            TILDA65=(Token)match(input,TILDA,FOLLOW_TILDA_in_fuzzy2720); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA65);

            pushFollow(FOLLOW_number_in_fuzzy2722);
            number66=number();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_number.add(number66.getTree());


            // AST REWRITE
            // elements: number
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 512:17: -> ^( FUZZY number )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:513:25: ^( FUZZY number )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUZZY, "FUZZY"), root_1);

                adaptor.addChild(root_1, stream_number.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fuzzy"

    public static class slop_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "slop"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:1: slop : TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) ;
    public final FTSParser.slop_return slop() throws RecognitionException {
        FTSParser.slop_return retval = new FTSParser.slop_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA67=null;
        Token DECIMAL_INTEGER_LITERAL68=null;

        Object TILDA67_tree=null;
        Object DECIMAL_INTEGER_LITERAL68_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleTokenStream stream_DECIMAL_INTEGER_LITERAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL_INTEGER_LITERAL");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:9: ( TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:518:9: TILDA DECIMAL_INTEGER_LITERAL
            {
            TILDA67=(Token)match(input,TILDA,FOLLOW_TILDA_in_slop2803); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA67);

            DECIMAL_INTEGER_LITERAL68=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2805); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL68);



            // AST REWRITE
            // elements: DECIMAL_INTEGER_LITERAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 519:17: -> ^( FUZZY DECIMAL_INTEGER_LITERAL )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:520:25: ^( FUZZY DECIMAL_INTEGER_LITERAL )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUZZY, "FUZZY"), root_1);

                adaptor.addChild(root_1, stream_DECIMAL_INTEGER_LITERAL.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "slop"

    public static class boost_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boost"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:1: boost : CARAT number -> ^( BOOST number ) ;
    public final FTSParser.boost_return boost() throws RecognitionException {
        FTSParser.boost_return retval = new FTSParser.boost_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CARAT69=null;
        FTSParser.number_return number70 = null;


        Object CARAT69_tree=null;
        RewriteRuleTokenStream stream_CARAT=new RewriteRuleTokenStream(adaptor,"token CARAT");
        RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:9: ( CARAT number -> ^( BOOST number ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:525:9: CARAT number
            {
            CARAT69=(Token)match(input,CARAT,FOLLOW_CARAT_in_boost2886); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CARAT.add(CARAT69);

            pushFollow(FOLLOW_number_in_boost2888);
            number70=number();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_number.add(number70.getTree());


            // AST REWRITE
            // elements: number
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 526:17: -> ^( BOOST number )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:527:25: ^( BOOST number )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BOOST, "BOOST"), root_1);

                adaptor.addChild(root_1, stream_number.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boost"

    public static class ftsTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:530:1: ftsTerm : ( fieldReference COLON )? ftsWord -> ftsWord ( fieldReference )? ;
    public final FTSParser.ftsTerm_return ftsTerm() throws RecognitionException {
        FTSParser.ftsTerm_return retval = new FTSParser.ftsTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON72=null;
        FTSParser.fieldReference_return fieldReference71 = null;

        FTSParser.ftsWord_return ftsWord73 = null;


        Object COLON72_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");
        RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:531:9: ( ( fieldReference COLON )? ftsWord -> ftsWord ( fieldReference )? )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:532:9: ( fieldReference COLON )? ftsWord
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:532:9: ( fieldReference COLON )?
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:532:10: fieldReference COLON
                    {
                    pushFollow(FOLLOW_fieldReference_in_ftsTerm2970);
                    fieldReference71=fieldReference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference71.getTree());
                    COLON72=(Token)match(input,COLON,FOLLOW_COLON_in_ftsTerm2972); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON72);


                    }
                    break;

            }

            pushFollow(FOLLOW_ftsWord_in_ftsTerm2976);
            ftsWord73=ftsWord();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord73.getTree());


            // AST REWRITE
            // elements: fieldReference, ftsWord
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 533:17: -> ftsWord ( fieldReference )?
            {
                adaptor.addChild(root_0, stream_ftsWord.nextTree());
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:533:28: ( fieldReference )?
                if ( stream_fieldReference.hasNext() ) {
                    adaptor.addChild(root_0, stream_fieldReference.nextTree());

                }
                stream_fieldReference.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsTerm"

    public static class cmisTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:536:1: cmisTerm : ftsWord -> ftsWord ;
    public final FTSParser.cmisTerm_return cmisTerm() throws RecognitionException {
        FTSParser.cmisTerm_return retval = new FTSParser.cmisTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsWord_return ftsWord74 = null;


        RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:9: ( ftsWord -> ftsWord )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:9: ftsWord
            {
            pushFollow(FOLLOW_ftsWord_in_cmisTerm3032);
            ftsWord74=ftsWord();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord74.getTree());


            // AST REWRITE
            // elements: ftsWord
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 539:17: -> ftsWord
            {
                adaptor.addChild(root_0, stream_ftsWord.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisTerm"

    public static class ftsExactTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsExactTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:1: ftsExactTerm : EQUALS ftsTerm -> ftsTerm ;
    public final FTSParser.ftsExactTerm_return ftsExactTerm() throws RecognitionException {
        FTSParser.ftsExactTerm_return retval = new FTSParser.ftsExactTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS75=null;
        FTSParser.ftsTerm_return ftsTerm76 = null;


        Object EQUALS75_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_ftsTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:543:9: ( EQUALS ftsTerm -> ftsTerm )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:9: EQUALS ftsTerm
            {
            EQUALS75=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsExactTerm3085); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS75);

            pushFollow(FOLLOW_ftsTerm_in_ftsExactTerm3087);
            ftsTerm76=ftsTerm();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsTerm.add(ftsTerm76.getTree());


            // AST REWRITE
            // elements: ftsTerm
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 545:17: -> ftsTerm
            {
                adaptor.addChild(root_0, stream_ftsTerm.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsExactTerm"

    public static class ftsPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:548:1: ftsPhrase : ( fieldReference COLON )? FTSPHRASE -> FTSPHRASE ( fieldReference )? ;
    public final FTSParser.ftsPhrase_return ftsPhrase() throws RecognitionException {
        FTSParser.ftsPhrase_return retval = new FTSParser.ftsPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON78=null;
        Token FTSPHRASE79=null;
        FTSParser.fieldReference_return fieldReference77 = null;


        Object COLON78_tree=null;
        Object FTSPHRASE79_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");
        RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:549:9: ( ( fieldReference COLON )? FTSPHRASE -> FTSPHRASE ( fieldReference )? )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:550:9: ( fieldReference COLON )? FTSPHRASE
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:550:9: ( fieldReference COLON )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ID||(LA29_0>=AT && LA29_0<=URI)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:550:10: fieldReference COLON
                    {
                    pushFollow(FOLLOW_fieldReference_in_ftsPhrase3141);
                    fieldReference77=fieldReference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference77.getTree());
                    COLON78=(Token)match(input,COLON,FOLLOW_COLON_in_ftsPhrase3143); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON78);


                    }
                    break;

            }

            FTSPHRASE79=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsPhrase3147); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE79);



            // AST REWRITE
            // elements: fieldReference, FTSPHRASE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 551:17: -> FTSPHRASE ( fieldReference )?
            {
                adaptor.addChild(root_0, stream_FTSPHRASE.nextNode());
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:551:30: ( fieldReference )?
                if ( stream_fieldReference.hasNext() ) {
                    adaptor.addChild(root_0, stream_fieldReference.nextTree());

                }
                stream_fieldReference.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsPhrase"

    public static class ftsExactPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsExactPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:554:1: ftsExactPhrase : EQUALS ftsPhrase -> ftsPhrase ;
    public final FTSParser.ftsExactPhrase_return ftsExactPhrase() throws RecognitionException {
        FTSParser.ftsExactPhrase_return retval = new FTSParser.ftsExactPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS80=null;
        FTSParser.ftsPhrase_return ftsPhrase81 = null;


        Object EQUALS80_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_ftsPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsPhrase");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:555:9: ( EQUALS ftsPhrase -> ftsPhrase )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:556:9: EQUALS ftsPhrase
            {
            EQUALS80=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsExactPhrase3211); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS80);

            pushFollow(FOLLOW_ftsPhrase_in_ftsExactPhrase3213);
            ftsPhrase81=ftsPhrase();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsPhrase.add(ftsPhrase81.getTree());


            // AST REWRITE
            // elements: ftsPhrase
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 557:17: -> ftsPhrase
            {
                adaptor.addChild(root_0, stream_ftsPhrase.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsExactPhrase"

    public static class ftsTokenisedPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsTokenisedPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:1: ftsTokenisedPhrase : TILDA ftsPhrase -> ftsPhrase ;
    public final FTSParser.ftsTokenisedPhrase_return ftsTokenisedPhrase() throws RecognitionException {
        FTSParser.ftsTokenisedPhrase_return retval = new FTSParser.ftsTokenisedPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA82=null;
        FTSParser.ftsPhrase_return ftsPhrase83 = null;


        Object TILDA82_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleSubtreeStream stream_ftsPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsPhrase");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:561:9: ( TILDA ftsPhrase -> ftsPhrase )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:9: TILDA ftsPhrase
            {
            TILDA82=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsTokenisedPhrase3274); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA82);

            pushFollow(FOLLOW_ftsPhrase_in_ftsTokenisedPhrase3276);
            ftsPhrase83=ftsPhrase();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsPhrase.add(ftsPhrase83.getTree());


            // AST REWRITE
            // elements: ftsPhrase
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 563:17: -> ftsPhrase
            {
                adaptor.addChild(root_0, stream_ftsPhrase.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsTokenisedPhrase"

    public static class cmisPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cmisPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:1: cmisPhrase : FTSPHRASE -> FTSPHRASE ;
    public final FTSParser.cmisPhrase_return cmisPhrase() throws RecognitionException {
        FTSParser.cmisPhrase_return retval = new FTSParser.cmisPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FTSPHRASE84=null;

        Object FTSPHRASE84_tree=null;
        RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:568:9: ( FTSPHRASE -> FTSPHRASE )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:9: FTSPHRASE
            {
            FTSPHRASE84=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_cmisPhrase3330); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE84);



            // AST REWRITE
            // elements: FTSPHRASE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 570:17: -> FTSPHRASE
            {
                adaptor.addChild(root_0, stream_FTSPHRASE.nextNode());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "cmisPhrase"

    public static class ftsSynonym_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsSynonym"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:573:1: ftsSynonym : TILDA ftsTerm -> ftsTerm ;
    public final FTSParser.ftsSynonym_return ftsSynonym() throws RecognitionException {
        FTSParser.ftsSynonym_return retval = new FTSParser.ftsSynonym_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA85=null;
        FTSParser.ftsTerm_return ftsTerm86 = null;


        Object TILDA85_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleSubtreeStream stream_ftsTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:574:9: ( TILDA ftsTerm -> ftsTerm )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:575:9: TILDA ftsTerm
            {
            TILDA85=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsSynonym3383); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA85);

            pushFollow(FOLLOW_ftsTerm_in_ftsSynonym3385);
            ftsTerm86=ftsTerm();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsTerm.add(ftsTerm86.getTree());


            // AST REWRITE
            // elements: ftsTerm
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 576:17: -> ftsTerm
            {
                adaptor.addChild(root_0, stream_ftsTerm.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsSynonym"

    public static class ftsRange_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsRange"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:579:1: ftsRange : ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? ;
    public final FTSParser.ftsRange_return ftsRange() throws RecognitionException {
        FTSParser.ftsRange_return retval = new FTSParser.ftsRange_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON88=null;
        FTSParser.fieldReference_return fieldReference87 = null;

        FTSParser.ftsFieldGroupRange_return ftsFieldGroupRange89 = null;


        Object COLON88_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_ftsFieldGroupRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupRange");
        RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:580:9: ( ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:581:9: ( fieldReference COLON )? ftsFieldGroupRange
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:581:9: ( fieldReference COLON )?
            int alt30=2;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:581:10: fieldReference COLON
                    {
                    pushFollow(FOLLOW_fieldReference_in_ftsRange3439);
                    fieldReference87=fieldReference();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference87.getTree());
                    COLON88=(Token)match(input,COLON,FOLLOW_COLON_in_ftsRange3441); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON88);


                    }
                    break;

            }

            pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsRange3445);
            ftsFieldGroupRange89=ftsFieldGroupRange();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupRange.add(ftsFieldGroupRange89.getTree());


            // AST REWRITE
            // elements: ftsFieldGroupRange, fieldReference
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 582:17: -> ftsFieldGroupRange ( fieldReference )?
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupRange.nextTree());
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:582:39: ( fieldReference )?
                if ( stream_fieldReference.hasNext() ) {
                    adaptor.addChild(root_0, stream_fieldReference.nextTree());

                }
                stream_fieldReference.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsRange"

    public static class ftsFieldGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroup"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:585:1: ftsFieldGroup : fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) ;
    public final FTSParser.ftsFieldGroup_return ftsFieldGroup() throws RecognitionException {
        FTSParser.ftsFieldGroup_return retval = new FTSParser.ftsFieldGroup_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON91=null;
        Token LPAREN92=null;
        Token RPAREN94=null;
        FTSParser.fieldReference_return fieldReference90 = null;

        FTSParser.ftsFieldGroupDisjunction_return ftsFieldGroupDisjunction93 = null;


        Object COLON91_tree=null;
        Object LPAREN92_tree=null;
        Object RPAREN94_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");
        RewriteRuleSubtreeStream stream_ftsFieldGroupDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupDisjunction");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:586:9: ( fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:587:9: fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN
            {
            pushFollow(FOLLOW_fieldReference_in_ftsFieldGroup3501);
            fieldReference90=fieldReference();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference90.getTree());
            COLON91=(Token)match(input,COLON,FOLLOW_COLON_in_ftsFieldGroup3503); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON91);

            LPAREN92=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroup3505); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN92);

            pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3507);
            ftsFieldGroupDisjunction93=ftsFieldGroupDisjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction93.getTree());
            RPAREN94=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroup3509); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN94);



            // AST REWRITE
            // elements: fieldReference, ftsFieldGroupDisjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 588:17: -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:589:25: ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_GROUP, "FIELD_GROUP"), root_1);

                adaptor.addChild(root_1, stream_fieldReference.nextTree());
                adaptor.addChild(root_1, stream_ftsFieldGroupDisjunction.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroup"

    public static class ftsFieldGroupDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:592:1: ftsFieldGroupDisjunction : ({...}? => ftsFieldGroupExplicitDisjunction | {...}? => ftsFieldGroupImplicitDisjunction );
    public final FTSParser.ftsFieldGroupDisjunction_return ftsFieldGroupDisjunction() throws RecognitionException {
        FTSParser.ftsFieldGroupDisjunction_return retval = new FTSParser.ftsFieldGroupDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsFieldGroupExplicitDisjunction_return ftsFieldGroupExplicitDisjunction95 = null;

        FTSParser.ftsFieldGroupImplicitDisjunction_return ftsFieldGroupImplicitDisjunction96 = null;



        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:593:9: ({...}? => ftsFieldGroupExplicitDisjunction | {...}? => ftsFieldGroupImplicitDisjunction )
            int alt31=2;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:594:9: {...}? => ftsFieldGroupExplicitDisjunction
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((defaultFieldConjunction() == true)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == true");
                    }
                    pushFollow(FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3595);
                    ftsFieldGroupExplicitDisjunction95=ftsFieldGroupExplicitDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsFieldGroupExplicitDisjunction95.getTree());

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:595:11: {...}? => ftsFieldGroupImplicitDisjunction
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !((defaultFieldConjunction() == false)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == false");
                    }
                    pushFollow(FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3610);
                    ftsFieldGroupImplicitDisjunction96=ftsFieldGroupImplicitDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsFieldGroupImplicitDisjunction96.getTree());

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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupDisjunction"

    public static class ftsFieldGroupExplicitDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupExplicitDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:598:1: ftsFieldGroupExplicitDisjunction : ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) ;
    public final FTSParser.ftsFieldGroupExplicitDisjunction_return ftsFieldGroupExplicitDisjunction() throws RecognitionException {
        FTSParser.ftsFieldGroupExplicitDisjunction_return retval = new FTSParser.ftsFieldGroupExplicitDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsFieldGroupImplicitConjunction_return ftsFieldGroupImplicitConjunction97 = null;

        FTSParser.or_return or98 = null;

        FTSParser.ftsFieldGroupImplicitConjunction_return ftsFieldGroupImplicitConjunction99 = null;


        RewriteRuleSubtreeStream stream_ftsFieldGroupImplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupImplicitConjunction");
        RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:599:9: ( ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:600:9: ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )*
            {
            pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3643);
            ftsFieldGroupImplicitConjunction97=ftsFieldGroupImplicitConjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupImplicitConjunction.add(ftsFieldGroupImplicitConjunction97.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:600:42: ( or ftsFieldGroupImplicitConjunction )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==BAR||LA32_0==OR) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:600:43: or ftsFieldGroupImplicitConjunction
            	    {
            	    pushFollow(FOLLOW_or_in_ftsFieldGroupExplicitDisjunction3646);
            	    or98=or();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_or.add(or98.getTree());
            	    pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3648);
            	    ftsFieldGroupImplicitConjunction99=ftsFieldGroupImplicitConjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsFieldGroupImplicitConjunction.add(ftsFieldGroupImplicitConjunction99.getTree());

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);



            // AST REWRITE
            // elements: ftsFieldGroupImplicitConjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 601:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:602:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DISJUNCTION, "FIELD_DISJUNCTION"), root_1);

                if ( !(stream_ftsFieldGroupImplicitConjunction.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsFieldGroupImplicitConjunction.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsFieldGroupImplicitConjunction.nextTree());

                }
                stream_ftsFieldGroupImplicitConjunction.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupExplicitDisjunction"

    public static class ftsFieldGroupImplicitDisjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupImplicitDisjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:605:1: ftsFieldGroupImplicitDisjunction : ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) ;
    public final FTSParser.ftsFieldGroupImplicitDisjunction_return ftsFieldGroupImplicitDisjunction() throws RecognitionException {
        FTSParser.ftsFieldGroupImplicitDisjunction_return retval = new FTSParser.ftsFieldGroupImplicitDisjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.or_return or100 = null;

        FTSParser.ftsFieldGroupExplicitConjunction_return ftsFieldGroupExplicitConjunction101 = null;


        RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
        RewriteRuleSubtreeStream stream_ftsFieldGroupExplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExplicitConjunction");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:606:9: ( ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
            int cnt34=0;
            loop34:
            do {
                int alt34=2;
                alt34 = dfa34.predict(input);
                switch (alt34) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:10: ( or )? ftsFieldGroupExplicitConjunction
            	    {
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:10: ( or )?
            	    int alt33=2;
            	    alt33 = dfa33.predict(input);
            	    switch (alt33) {
            	        case 1 :
            	            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:10: or
            	            {
            	            pushFollow(FOLLOW_or_in_ftsFieldGroupImplicitDisjunction3733);
            	            or100=or();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_or.add(or100.getTree());

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction3736);
            	    ftsFieldGroupExplicitConjunction101=ftsFieldGroupExplicitConjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsFieldGroupExplicitConjunction.add(ftsFieldGroupExplicitConjunction101.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);



            // AST REWRITE
            // elements: ftsFieldGroupExplicitConjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 608:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:609:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DISJUNCTION, "FIELD_DISJUNCTION"), root_1);

                if ( !(stream_ftsFieldGroupExplicitConjunction.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsFieldGroupExplicitConjunction.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsFieldGroupExplicitConjunction.nextTree());

                }
                stream_ftsFieldGroupExplicitConjunction.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupImplicitDisjunction"

    public static class ftsFieldGroupExplicitConjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupExplicitConjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:616:1: ftsFieldGroupExplicitConjunction : ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
    public final FTSParser.ftsFieldGroupExplicitConjunction_return ftsFieldGroupExplicitConjunction() throws RecognitionException {
        FTSParser.ftsFieldGroupExplicitConjunction_return retval = new FTSParser.ftsFieldGroupExplicitConjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsFieldGroupPrefixed_return ftsFieldGroupPrefixed102 = null;

        FTSParser.and_return and103 = null;

        FTSParser.ftsFieldGroupPrefixed_return ftsFieldGroupPrefixed104 = null;


        RewriteRuleSubtreeStream stream_ftsFieldGroupPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPrefixed");
        RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:9: ( ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:618:9: ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )*
            {
            pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction3823);
            ftsFieldGroupPrefixed102=ftsFieldGroupPrefixed();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed102.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:618:31: ( and ftsFieldGroupPrefixed )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0>=AND && LA35_0<=AMP)) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:618:32: and ftsFieldGroupPrefixed
            	    {
            	    pushFollow(FOLLOW_and_in_ftsFieldGroupExplicitConjunction3826);
            	    and103=and();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_and.add(and103.getTree());
            	    pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction3828);
            	    ftsFieldGroupPrefixed104=ftsFieldGroupPrefixed();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed104.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);



            // AST REWRITE
            // elements: ftsFieldGroupPrefixed
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 619:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:620:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_CONJUNCTION, "FIELD_CONJUNCTION"), root_1);

                if ( !(stream_ftsFieldGroupPrefixed.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsFieldGroupPrefixed.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsFieldGroupPrefixed.nextTree());

                }
                stream_ftsFieldGroupPrefixed.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupExplicitConjunction"

    public static class ftsFieldGroupImplicitConjunction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupImplicitConjunction"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:623:1: ftsFieldGroupImplicitConjunction : ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
    public final FTSParser.ftsFieldGroupImplicitConjunction_return ftsFieldGroupImplicitConjunction() throws RecognitionException {
        FTSParser.ftsFieldGroupImplicitConjunction_return retval = new FTSParser.ftsFieldGroupImplicitConjunction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.and_return and105 = null;

        FTSParser.ftsFieldGroupPrefixed_return ftsFieldGroupPrefixed106 = null;


        RewriteRuleSubtreeStream stream_ftsFieldGroupPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPrefixed");
        RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:624:9: ( ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:9: ( ( and )? ftsFieldGroupPrefixed )+
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:9: ( ( and )? ftsFieldGroupPrefixed )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                alt37 = dfa37.predict(input);
                switch (alt37) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:10: ( and )? ftsFieldGroupPrefixed
            	    {
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:10: ( and )?
            	    int alt36=2;
            	    alt36 = dfa36.predict(input);
            	    switch (alt36) {
            	        case 1 :
            	            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:10: and
            	            {
            	            pushFollow(FOLLOW_and_in_ftsFieldGroupImplicitConjunction3913);
            	            and105=and();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_and.add(and105.getTree());

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction3916);
            	    ftsFieldGroupPrefixed106=ftsFieldGroupPrefixed();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed106.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt37 >= 1 ) break loop37;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
            } while (true);



            // AST REWRITE
            // elements: ftsFieldGroupPrefixed
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 626:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:627:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_CONJUNCTION, "FIELD_CONJUNCTION"), root_1);

                if ( !(stream_ftsFieldGroupPrefixed.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ftsFieldGroupPrefixed.hasNext() ) {
                    adaptor.addChild(root_1, stream_ftsFieldGroupPrefixed.nextTree());

                }
                stream_ftsFieldGroupPrefixed.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupImplicitConjunction"

    public static class ftsFieldGroupPrefixed_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupPrefixed"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:630:1: ftsFieldGroupPrefixed : ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) );
    public final FTSParser.ftsFieldGroupPrefixed_return ftsFieldGroupPrefixed() throws RecognitionException {
        FTSParser.ftsFieldGroupPrefixed_return retval = new FTSParser.ftsFieldGroupPrefixed_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS112=null;
        Token BAR115=null;
        Token MINUS118=null;
        FTSParser.not_return not107 = null;

        FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest108 = null;

        FTSParser.boost_return boost109 = null;

        FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest110 = null;

        FTSParser.boost_return boost111 = null;

        FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest113 = null;

        FTSParser.boost_return boost114 = null;

        FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest116 = null;

        FTSParser.boost_return boost117 = null;

        FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest119 = null;

        FTSParser.boost_return boost120 = null;


        Object PLUS112_tree=null;
        Object BAR115_tree=null;
        Object MINUS118_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_not=new RewriteRuleSubtreeStream(adaptor,"rule not");
        RewriteRuleSubtreeStream stream_boost=new RewriteRuleSubtreeStream(adaptor,"rule boost");
        RewriteRuleSubtreeStream stream_ftsFieldGroupTest=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTest");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:631:9: ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) )
            int alt43=5;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:9: ( not )=> not ftsFieldGroupTest ( boost )?
                    {
                    pushFollow(FOLLOW_not_in_ftsFieldGroupPrefixed4006);
                    not107=not();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_not.add(not107.getTree());
                    pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4008);
                    ftsFieldGroupTest108=ftsFieldGroupTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest108.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:40: ( boost )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==CARAT) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:40: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4010);
                            boost109=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost109.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsFieldGroupTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 633:17: -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:634:25: ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_NEGATION, "FIELD_NEGATION"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:634:60: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:11: ftsFieldGroupTest ( boost )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4074);
                    ftsFieldGroupTest110=ftsFieldGroupTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest110.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:29: ( boost )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==CARAT) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:29: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4076);
                            boost111=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost111.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsFieldGroupTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 636:17: -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:637:25: ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DEFAULT, "FIELD_DEFAULT"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:637:59: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:638:11: PLUS ftsFieldGroupTest ( boost )?
                    {
                    PLUS112=(Token)match(input,PLUS,FOLLOW_PLUS_in_ftsFieldGroupPrefixed4140); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS112);

                    pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4142);
                    ftsFieldGroupTest113=ftsFieldGroupTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest113.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:638:34: ( boost )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==CARAT) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:638:34: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4144);
                            boost114=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost114.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsFieldGroupTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 639:17: -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:25: ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_MANDATORY, "FIELD_MANDATORY"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:61: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:641:11: BAR ftsFieldGroupTest ( boost )?
                    {
                    BAR115=(Token)match(input,BAR,FOLLOW_BAR_in_ftsFieldGroupPrefixed4208); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BAR.add(BAR115);

                    pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4210);
                    ftsFieldGroupTest116=ftsFieldGroupTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest116.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:641:33: ( boost )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==CARAT) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:641:33: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4212);
                            boost117=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost117.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsFieldGroupTest, boost
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 642:17: -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:643:25: ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_OPTIONAL, "FIELD_OPTIONAL"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:643:60: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:644:11: MINUS ftsFieldGroupTest ( boost )?
                    {
                    MINUS118=(Token)match(input,MINUS,FOLLOW_MINUS_in_ftsFieldGroupPrefixed4276); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS.add(MINUS118);

                    pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4278);
                    ftsFieldGroupTest119=ftsFieldGroupTest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest119.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:644:35: ( boost )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==CARAT) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:644:35: boost
                            {
                            pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4280);
                            boost120=boost();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_boost.add(boost120.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: boost, ftsFieldGroupTest
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 645:17: -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:646:25: ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_EXCLUDE, "FIELD_EXCLUDE"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:646:59: ( boost )?
                        if ( stream_boost.hasNext() ) {
                            adaptor.addChild(root_1, stream_boost.nextTree());

                        }
                        stream_boost.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupPrefixed"

    public static class ftsFieldGroupTest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupTest"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:649:1: ftsFieldGroupTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction );
    public final FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest() throws RecognitionException {
        FTSParser.ftsFieldGroupTest_return retval = new FTSParser.ftsFieldGroupTest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LPAREN135=null;
        Token RPAREN137=null;
        FTSParser.ftsFieldGroupProximity_return ftsFieldGroupProximity121 = null;

        FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm122 = null;

        FTSParser.fuzzy_return fuzzy123 = null;

        FTSParser.ftsFieldGroupExactTerm_return ftsFieldGroupExactTerm124 = null;

        FTSParser.fuzzy_return fuzzy125 = null;

        FTSParser.ftsFieldGroupPhrase_return ftsFieldGroupPhrase126 = null;

        FTSParser.slop_return slop127 = null;

        FTSParser.ftsFieldGroupExactPhrase_return ftsFieldGroupExactPhrase128 = null;

        FTSParser.slop_return slop129 = null;

        FTSParser.ftsFieldGroupTokenisedPhrase_return ftsFieldGroupTokenisedPhrase130 = null;

        FTSParser.slop_return slop131 = null;

        FTSParser.ftsFieldGroupSynonym_return ftsFieldGroupSynonym132 = null;

        FTSParser.fuzzy_return fuzzy133 = null;

        FTSParser.ftsFieldGroupRange_return ftsFieldGroupRange134 = null;

        FTSParser.ftsFieldGroupDisjunction_return ftsFieldGroupDisjunction136 = null;


        Object LPAREN135_tree=null;
        Object RPAREN137_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_ftsFieldGroupRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupRange");
        RewriteRuleSubtreeStream stream_ftsFieldGroupPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPhrase");
        RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");
        RewriteRuleSubtreeStream stream_ftsFieldGroupTokenisedPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTokenisedPhrase");
        RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
        RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
        RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");
        RewriteRuleSubtreeStream stream_ftsFieldGroupSynonym=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupSynonym");
        RewriteRuleSubtreeStream stream_ftsFieldGroupExactTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactTerm");
        RewriteRuleSubtreeStream stream_ftsFieldGroupDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupDisjunction");
        RewriteRuleSubtreeStream stream_ftsFieldGroupProximity=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximity");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:650:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction )
            int alt50=9;
            alt50 = dfa50.predict(input);
            switch (alt50) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:9: ( ftsFieldGroupProximity )=> ftsFieldGroupProximity
                    {
                    pushFollow(FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4371);
                    ftsFieldGroupProximity121=ftsFieldGroupProximity();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupProximity.add(ftsFieldGroupProximity121.getTree());


                    // AST REWRITE
                    // elements: ftsFieldGroupProximity
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 652:17: -> ^( FG_PROXIMITY ftsFieldGroupProximity )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:653:25: ^( FG_PROXIMITY ftsFieldGroupProximity )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PROXIMITY, "FG_PROXIMITY"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupProximity.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:11: ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4431);
                    ftsFieldGroupTerm122=ftsFieldGroupTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm122.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:29: ( ( fuzzy )=> fuzzy )?
                    int alt44=2;
                    alt44 = dfa44.predict(input);
                    switch (alt44) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:31: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4441);
                            fuzzy123=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy123.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: fuzzy, ftsFieldGroupTerm
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 655:17: -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:656:25: ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_TERM, "FG_TERM"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTerm.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:656:53: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:11: ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4506);
                    ftsFieldGroupExactTerm124=ftsFieldGroupExactTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupExactTerm.add(ftsFieldGroupExactTerm124.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:34: ( ( fuzzy )=> fuzzy )?
                    int alt45=2;
                    alt45 = dfa45.predict(input);
                    switch (alt45) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:36: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4516);
                            fuzzy125=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy125.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: fuzzy, ftsFieldGroupExactTerm
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 658:17: -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:25: ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_TERM, "FG_EXACT_TERM"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupExactTerm.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:64: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:11: ftsFieldGroupPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4581);
                    ftsFieldGroupPhrase126=ftsFieldGroupPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupPhrase.add(ftsFieldGroupPhrase126.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:31: ( ( slop )=> slop )?
                    int alt46=2;
                    alt46 = dfa46.predict(input);
                    switch (alt46) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:33: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4591);
                            slop127=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop127.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: slop, ftsFieldGroupPhrase
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 661:17: -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:25: ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:57: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:11: ftsFieldGroupExactPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest4656);
                    ftsFieldGroupExactPhrase128=ftsFieldGroupExactPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase128.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:36: ( ( slop )=> slop )?
                    int alt47=2;
                    alt47 = dfa47.predict(input);
                    switch (alt47) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:38: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4666);
                            slop129=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop129.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: slop, ftsFieldGroupExactPhrase
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 664:17: -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:25: ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_PHRASE, "FG_EXACT_PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupExactPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:68: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:11: ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest4731);
                    ftsFieldGroupTokenisedPhrase130=ftsFieldGroupTokenisedPhrase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupTokenisedPhrase.add(ftsFieldGroupTokenisedPhrase130.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:40: ( ( slop )=> slop )?
                    int alt48=2;
                    alt48 = dfa48.predict(input);
                    switch (alt48) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:42: ( slop )=> slop
                            {
                            pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4741);
                            slop131=slop();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_slop.add(slop131.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsFieldGroupTokenisedPhrase, slop
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 667:17: -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:25: ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupTokenisedPhrase.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:66: ( slop )?
                        if ( stream_slop.hasNext() ) {
                            adaptor.addChild(root_1, stream_slop.nextTree());

                        }
                        stream_slop.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:11: ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )?
                    {
                    pushFollow(FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest4806);
                    ftsFieldGroupSynonym132=ftsFieldGroupSynonym();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupSynonym.add(ftsFieldGroupSynonym132.getTree());
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:32: ( ( fuzzy )=> fuzzy )?
                    int alt49=2;
                    alt49 = dfa49.predict(input);
                    switch (alt49) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:34: ( fuzzy )=> fuzzy
                            {
                            pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4816);
                            fuzzy133=fuzzy();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy133.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ftsFieldGroupSynonym, fuzzy
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 670:17: -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:25: ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_SYNONYM, "FG_SYNONYM"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupSynonym.nextTree());
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:59: ( fuzzy )?
                        if ( stream_fuzzy.hasNext() ) {
                            adaptor.addChild(root_1, stream_fuzzy.nextTree());

                        }
                        stream_fuzzy.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:672:11: ftsFieldGroupRange
                    {
                    pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest4881);
                    ftsFieldGroupRange134=ftsFieldGroupRange();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupRange.add(ftsFieldGroupRange134.getTree());


                    // AST REWRITE
                    // elements: ftsFieldGroupRange
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 673:17: -> ^( FG_RANGE ftsFieldGroupRange )
                    {
                        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:25: ^( FG_RANGE ftsFieldGroupRange )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_RANGE, "FG_RANGE"), root_1);

                        adaptor.addChild(root_1, stream_ftsFieldGroupRange.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:675:11: LPAREN ftsFieldGroupDisjunction RPAREN
                    {
                    LPAREN135=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroupTest4941); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN135);

                    pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest4943);
                    ftsFieldGroupDisjunction136=ftsFieldGroupDisjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction136.getTree());
                    RPAREN137=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroupTest4945); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN137);



                    // AST REWRITE
                    // elements: ftsFieldGroupDisjunction
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 676:17: -> ftsFieldGroupDisjunction
                    {
                        adaptor.addChild(root_0, stream_ftsFieldGroupDisjunction.nextTree());

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupTest"

    public static class ftsFieldGroupTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:1: ftsFieldGroupTerm : ftsWord ;
    public final FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm() throws RecognitionException {
        FTSParser.ftsFieldGroupTerm_return retval = new FTSParser.ftsFieldGroupTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsWord_return ftsWord138 = null;



        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:680:9: ( ftsWord )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:681:9: ftsWord
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_ftsWord_in_ftsFieldGroupTerm4998);
            ftsWord138=ftsWord();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWord138.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupTerm"

    public static class ftsFieldGroupExactTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupExactTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:684:1: ftsFieldGroupExactTerm : EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm ;
    public final FTSParser.ftsFieldGroupExactTerm_return ftsFieldGroupExactTerm() throws RecognitionException {
        FTSParser.ftsFieldGroupExactTerm_return retval = new FTSParser.ftsFieldGroupExactTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS139=null;
        FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm140 = null;


        Object EQUALS139_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:685:9: ( EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:686:9: EQUALS ftsFieldGroupTerm
            {
            EQUALS139=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5031); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS139);

            pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5033);
            ftsFieldGroupTerm140=ftsFieldGroupTerm();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm140.getTree());


            // AST REWRITE
            // elements: ftsFieldGroupTerm
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 687:17: -> ftsFieldGroupTerm
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupTerm.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupExactTerm"

    public static class ftsFieldGroupPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:690:1: ftsFieldGroupPhrase : FTSPHRASE ;
    public final FTSParser.ftsFieldGroupPhrase_return ftsFieldGroupPhrase() throws RecognitionException {
        FTSParser.ftsFieldGroupPhrase_return retval = new FTSParser.ftsFieldGroupPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FTSPHRASE141=null;

        Object FTSPHRASE141_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:691:9: ( FTSPHRASE )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:692:9: FTSPHRASE
            {
            root_0 = (Object)adaptor.nil();

            FTSPHRASE141=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5086); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FTSPHRASE141_tree = (Object)adaptor.create(FTSPHRASE141);
            adaptor.addChild(root_0, FTSPHRASE141_tree);
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupPhrase"

    public static class ftsFieldGroupExactPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupExactPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:695:1: ftsFieldGroupExactPhrase : EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
    public final FTSParser.ftsFieldGroupExactPhrase_return ftsFieldGroupExactPhrase() throws RecognitionException {
        FTSParser.ftsFieldGroupExactPhrase_return retval = new FTSParser.ftsFieldGroupExactPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS142=null;
        FTSParser.ftsFieldGroupExactPhrase_return ftsFieldGroupExactPhrase143 = null;


        Object EQUALS142_tree=null;
        RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
        RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:696:9: ( EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:697:9: EQUALS ftsFieldGroupExactPhrase
            {
            EQUALS142=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5127); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS142);

            pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5129);
            ftsFieldGroupExactPhrase143=ftsFieldGroupExactPhrase();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase143.getTree());


            // AST REWRITE
            // elements: ftsFieldGroupExactPhrase
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 698:17: -> ftsFieldGroupExactPhrase
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupExactPhrase.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupExactPhrase"

    public static class ftsFieldGroupTokenisedPhrase_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupTokenisedPhrase"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:701:1: ftsFieldGroupTokenisedPhrase : TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
    public final FTSParser.ftsFieldGroupTokenisedPhrase_return ftsFieldGroupTokenisedPhrase() throws RecognitionException {
        FTSParser.ftsFieldGroupTokenisedPhrase_return retval = new FTSParser.ftsFieldGroupTokenisedPhrase_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA144=null;
        FTSParser.ftsFieldGroupExactPhrase_return ftsFieldGroupExactPhrase145 = null;


        Object TILDA144_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:702:9: ( TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:703:9: TILDA ftsFieldGroupExactPhrase
            {
            TILDA144=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5190); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA144);

            pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5192);
            ftsFieldGroupExactPhrase145=ftsFieldGroupExactPhrase();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase145.getTree());


            // AST REWRITE
            // elements: ftsFieldGroupExactPhrase
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 704:17: -> ftsFieldGroupExactPhrase
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupExactPhrase.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupTokenisedPhrase"

    public static class ftsFieldGroupSynonym_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupSynonym"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:707:1: ftsFieldGroupSynonym : TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm ;
    public final FTSParser.ftsFieldGroupSynonym_return ftsFieldGroupSynonym() throws RecognitionException {
        FTSParser.ftsFieldGroupSynonym_return retval = new FTSParser.ftsFieldGroupSynonym_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDA146=null;
        FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm147 = null;


        Object TILDA146_tree=null;
        RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
        RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:708:9: ( TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:709:9: TILDA ftsFieldGroupTerm
            {
            TILDA146=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupSynonym5245); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TILDA.add(TILDA146);

            pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5247);
            ftsFieldGroupTerm147=ftsFieldGroupTerm();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm147.getTree());


            // AST REWRITE
            // elements: ftsFieldGroupTerm
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 710:17: -> ftsFieldGroupTerm
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupTerm.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupSynonym"

    public static class ftsFieldGroupProximity_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupProximity"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:713:1: ftsFieldGroupProximity : ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ ;
    public final FTSParser.ftsFieldGroupProximity_return ftsFieldGroupProximity() throws RecognitionException {
        FTSParser.ftsFieldGroupProximity_return retval = new FTSParser.ftsFieldGroupProximity_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        FTSParser.ftsFieldGroupProximityTerm_return ftsFieldGroupProximityTerm148 = null;

        FTSParser.proximityGroup_return proximityGroup149 = null;

        FTSParser.ftsFieldGroupProximityTerm_return ftsFieldGroupProximityTerm150 = null;


        RewriteRuleSubtreeStream stream_proximityGroup=new RewriteRuleSubtreeStream(adaptor,"rule proximityGroup");
        RewriteRuleSubtreeStream stream_ftsFieldGroupProximityTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximityTerm");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:714:9: ( ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:9: ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
            {
            pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5300);
            ftsFieldGroupProximityTerm148=ftsFieldGroupProximityTerm();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ftsFieldGroupProximityTerm.add(ftsFieldGroupProximityTerm148.getTree());
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:36: ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
            int cnt51=0;
            loop51:
            do {
                int alt51=2;
                alt51 = dfa51.predict(input);
                switch (alt51) {
            	case 1 :
            	    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:38: ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm
            	    {
            	    pushFollow(FOLLOW_proximityGroup_in_ftsFieldGroupProximity5310);
            	    proximityGroup149=proximityGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_proximityGroup.add(proximityGroup149.getTree());
            	    pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5312);
            	    ftsFieldGroupProximityTerm150=ftsFieldGroupProximityTerm();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ftsFieldGroupProximityTerm.add(ftsFieldGroupProximityTerm150.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt51 >= 1 ) break loop51;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(51, input);
                        throw eee;
                }
                cnt51++;
            } while (true);



            // AST REWRITE
            // elements: proximityGroup, ftsFieldGroupProximityTerm, ftsFieldGroupProximityTerm
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 716:17: -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+
            {
                adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());
                if ( !(stream_proximityGroup.hasNext()||stream_ftsFieldGroupProximityTerm.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_proximityGroup.hasNext()||stream_ftsFieldGroupProximityTerm.hasNext() ) {
                    adaptor.addChild(root_0, stream_proximityGroup.nextTree());
                    adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());

                }
                stream_proximityGroup.reset();
                stream_ftsFieldGroupProximityTerm.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupProximity"

    public static class ftsFieldGroupProximityTerm_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupProximityTerm"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:719:1: ftsFieldGroupProximityTerm : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL );
    public final FTSParser.ftsFieldGroupProximityTerm_return ftsFieldGroupProximityTerm() throws RecognitionException {
        FTSParser.ftsFieldGroupProximityTerm_return retval = new FTSParser.ftsFieldGroupProximityTerm_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set151=null;

        Object set151_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:720:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            root_0 = (Object)adaptor.nil();

            set151=(Token)input.LT(1);
            if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||(input.LA(1)>=ID && input.LA(1)<=FLOATING_POINT_LITERAL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set151));
                state.errorRecovery=false;state.failed=false;
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupProximityTerm"

    public static class proximityGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "proximityGroup"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:731:1: proximityGroup : STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) ;
    public final FTSParser.proximityGroup_return proximityGroup() throws RecognitionException {
        FTSParser.proximityGroup_return retval = new FTSParser.proximityGroup_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STAR152=null;
        Token LPAREN153=null;
        Token DECIMAL_INTEGER_LITERAL154=null;
        Token RPAREN155=null;

        Object STAR152_tree=null;
        Object LPAREN153_tree=null;
        Object DECIMAL_INTEGER_LITERAL154_tree=null;
        Object RPAREN155_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_DECIMAL_INTEGER_LITERAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL_INTEGER_LITERAL");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:732:9: ( STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:733:9: STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
            {
            STAR152=(Token)match(input,STAR,FOLLOW_STAR_in_proximityGroup5491); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STAR.add(STAR152);

            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:733:14: ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==LPAREN) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:733:15: LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN
                    {
                    LPAREN153=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_proximityGroup5494); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN153);

                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:733:22: ( DECIMAL_INTEGER_LITERAL )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==DECIMAL_INTEGER_LITERAL) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:733:22: DECIMAL_INTEGER_LITERAL
                            {
                            DECIMAL_INTEGER_LITERAL154=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5496); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL154);


                            }
                            break;

                    }

                    RPAREN155=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_proximityGroup5499); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN155);


                    }
                    break;

            }



            // AST REWRITE
            // elements: DECIMAL_INTEGER_LITERAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 734:17: -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:735:25: ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROXIMITY, "PROXIMITY"), root_1);

                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:735:37: ( DECIMAL_INTEGER_LITERAL )?
                if ( stream_DECIMAL_INTEGER_LITERAL.hasNext() ) {
                    adaptor.addChild(root_1, stream_DECIMAL_INTEGER_LITERAL.nextNode());

                }
                stream_DECIMAL_INTEGER_LITERAL.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "proximityGroup"

    public static class ftsFieldGroupRange_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsFieldGroupRange"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:738:1: ftsFieldGroupRange : ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right );
    public final FTSParser.ftsFieldGroupRange_return ftsFieldGroupRange() throws RecognitionException {
        FTSParser.ftsFieldGroupRange_return retval = new FTSParser.ftsFieldGroupRange_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOTDOT157=null;
        Token TO161=null;
        FTSParser.ftsRangeWord_return ftsRangeWord156 = null;

        FTSParser.ftsRangeWord_return ftsRangeWord158 = null;

        FTSParser.range_left_return range_left159 = null;

        FTSParser.ftsRangeWord_return ftsRangeWord160 = null;

        FTSParser.ftsRangeWord_return ftsRangeWord162 = null;

        FTSParser.range_right_return range_right163 = null;


        Object DOTDOT157_tree=null;
        Object TO161_tree=null;
        RewriteRuleTokenStream stream_DOTDOT=new RewriteRuleTokenStream(adaptor,"token DOTDOT");
        RewriteRuleTokenStream stream_TO=new RewriteRuleTokenStream(adaptor,"token TO");
        RewriteRuleSubtreeStream stream_range_left=new RewriteRuleSubtreeStream(adaptor,"rule range_left");
        RewriteRuleSubtreeStream stream_range_right=new RewriteRuleSubtreeStream(adaptor,"rule range_right");
        RewriteRuleSubtreeStream stream_ftsRangeWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsRangeWord");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:739:9: ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==DECIMAL_INTEGER_LITERAL||(LA54_0>=FTSPHRASE && LA54_0<=FTSWILD)||LA54_0==FLOATING_POINT_LITERAL) ) {
                alt54=1;
            }
            else if ( ((LA54_0>=LSQUARE && LA54_0<=LT)) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:740:9: ftsRangeWord DOTDOT ftsRangeWord
                    {
                    pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5583);
                    ftsRangeWord156=ftsRangeWord();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord156.getTree());
                    DOTDOT157=(Token)match(input,DOTDOT,FOLLOW_DOTDOT_in_ftsFieldGroupRange5585); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOTDOT.add(DOTDOT157);

                    pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5587);
                    ftsRangeWord158=ftsRangeWord();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord158.getTree());


                    // AST REWRITE
                    // elements: ftsRangeWord, ftsRangeWord
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 741:17: -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
                        adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
                        adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
                        adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:11: range_left ftsRangeWord TO ftsRangeWord range_right
                    {
                    pushFollow(FOLLOW_range_left_in_ftsFieldGroupRange5625);
                    range_left159=range_left();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_range_left.add(range_left159.getTree());
                    pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5627);
                    ftsRangeWord160=ftsRangeWord();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord160.getTree());
                    TO161=(Token)match(input,TO,FOLLOW_TO_in_ftsFieldGroupRange5629); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TO.add(TO161);

                    pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5631);
                    ftsRangeWord162=ftsRangeWord();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord162.getTree());
                    pushFollow(FOLLOW_range_right_in_ftsFieldGroupRange5633);
                    range_right163=range_right();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_range_right.add(range_right163.getTree());


                    // AST REWRITE
                    // elements: ftsRangeWord, ftsRangeWord, range_right, range_left
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 743:17: -> range_left ftsRangeWord ftsRangeWord range_right
                    {
                        adaptor.addChild(root_0, stream_range_left.nextTree());
                        adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
                        adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
                        adaptor.addChild(root_0, stream_range_right.nextTree());

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsFieldGroupRange"

    public static class range_left_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range_left"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:746:1: range_left : ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE );
    public final FTSParser.range_left_return range_left() throws RecognitionException {
        FTSParser.range_left_return retval = new FTSParser.range_left_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LSQUARE164=null;
        Token LT165=null;

        Object LSQUARE164_tree=null;
        Object LT165_tree=null;
        RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:747:9: ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LSQUARE) ) {
                alt55=1;
            }
            else if ( (LA55_0==LT) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:748:9: LSQUARE
                    {
                    LSQUARE164=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_range_left5692); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LSQUARE.add(LSQUARE164);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 749:17: -> INCLUSIVE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:750:11: LT
                    {
                    LT165=(Token)match(input,LT,FOLLOW_LT_in_range_left5724); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LT.add(LT165);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 751:17: -> EXCLUSIVE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(EXCLUSIVE, "EXCLUSIVE"));

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range_left"

    public static class range_right_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range_right"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:754:1: range_right : ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE );
    public final FTSParser.range_right_return range_right() throws RecognitionException {
        FTSParser.range_right_return retval = new FTSParser.range_right_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token RSQUARE166=null;
        Token GT167=null;

        Object RSQUARE166_tree=null;
        Object GT167_tree=null;
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:755:9: ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==RSQUARE) ) {
                alt56=1;
            }
            else if ( (LA56_0==GT) ) {
                alt56=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:756:9: RSQUARE
                    {
                    RSQUARE166=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_range_right5777); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RSQUARE.add(RSQUARE166);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 757:17: -> INCLUSIVE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:758:11: GT
                    {
                    GT167=(Token)match(input,GT,FOLLOW_GT_in_range_right5809); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GT.add(GT167);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 759:17: -> EXCLUSIVE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(EXCLUSIVE, "EXCLUSIVE"));

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range_right"

    public static class fieldReference_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fieldReference"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:764:1: fieldReference : ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
    public final FTSParser.fieldReference_return fieldReference() throws RecognitionException {
        FTSParser.fieldReference_return retval = new FTSParser.fieldReference_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT168=null;
        FTSParser.prefix_return prefix169 = null;

        FTSParser.uri_return uri170 = null;

        FTSParser.identifier_return identifier171 = null;


        Object AT168_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleSubtreeStream stream_prefix=new RewriteRuleSubtreeStream(adaptor,"rule prefix");
        RewriteRuleSubtreeStream stream_uri=new RewriteRuleSubtreeStream(adaptor,"rule uri");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:765:9: ( ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:766:9: ( AT )? ( prefix | uri )? identifier
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:766:9: ( AT )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==AT) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:766:9: AT
                    {
                    AT168=(Token)match(input,AT,FOLLOW_AT_in_fieldReference5865); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AT.add(AT168);


                    }
                    break;

            }

            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:767:9: ( prefix | uri )?
            int alt58=3;
            alt58 = dfa58.predict(input);
            switch (alt58) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:768:17: prefix
                    {
                    pushFollow(FOLLOW_prefix_in_fieldReference5894);
                    prefix169=prefix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_prefix.add(prefix169.getTree());

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:769:19: uri
                    {
                    pushFollow(FOLLOW_uri_in_fieldReference5914);
                    uri170=uri();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_uri.add(uri170.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_fieldReference5935);
            identifier171=identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier171.getTree());


            // AST REWRITE
            // elements: uri, prefix, identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 772:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:773:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);

                adaptor.addChild(root_1, stream_identifier.nextTree());
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:773:48: ( prefix )?
                if ( stream_prefix.hasNext() ) {
                    adaptor.addChild(root_1, stream_prefix.nextTree());

                }
                stream_prefix.reset();
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:773:56: ( uri )?
                if ( stream_uri.hasNext() ) {
                    adaptor.addChild(root_1, stream_uri.nextTree());

                }
                stream_uri.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fieldReference"

    public static class tempReference_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tempReference"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:776:1: tempReference : ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
    public final FTSParser.tempReference_return tempReference() throws RecognitionException {
        FTSParser.tempReference_return retval = new FTSParser.tempReference_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AT172=null;
        FTSParser.prefix_return prefix173 = null;

        FTSParser.uri_return uri174 = null;

        FTSParser.identifier_return identifier175 = null;


        Object AT172_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleSubtreeStream stream_prefix=new RewriteRuleSubtreeStream(adaptor,"rule prefix");
        RewriteRuleSubtreeStream stream_uri=new RewriteRuleSubtreeStream(adaptor,"rule uri");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:9: ( ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:778:9: ( AT )? ( prefix | uri )? identifier
            {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:778:9: ( AT )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==AT) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:778:9: AT
                    {
                    AT172=(Token)match(input,AT,FOLLOW_AT_in_tempReference6022); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AT.add(AT172);


                    }
                    break;

            }

            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:779:9: ( prefix | uri )?
            int alt60=3;
            alt60 = dfa60.predict(input);
            switch (alt60) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:780:17: prefix
                    {
                    pushFollow(FOLLOW_prefix_in_tempReference6051);
                    prefix173=prefix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_prefix.add(prefix173.getTree());

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:781:19: uri
                    {
                    pushFollow(FOLLOW_uri_in_tempReference6071);
                    uri174=uri();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_uri.add(uri174.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_tempReference6092);
            identifier175=identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier175.getTree());


            // AST REWRITE
            // elements: prefix, identifier, uri
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 784:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:785:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);

                adaptor.addChild(root_1, stream_identifier.nextTree());
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:785:48: ( prefix )?
                if ( stream_prefix.hasNext() ) {
                    adaptor.addChild(root_1, stream_prefix.nextTree());

                }
                stream_prefix.reset();
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:785:56: ( uri )?
                if ( stream_uri.hasNext() ) {
                    adaptor.addChild(root_1, stream_uri.nextTree());

                }
                stream_uri.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "tempReference"

    public static class prefix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prefix"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:788:1: prefix : identifier COLON -> ^( PREFIX identifier ) ;
    public final FTSParser.prefix_return prefix() throws RecognitionException {
        FTSParser.prefix_return retval = new FTSParser.prefix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON177=null;
        FTSParser.identifier_return identifier176 = null;


        Object COLON177_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:789:9: ( identifier COLON -> ^( PREFIX identifier ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:790:9: identifier COLON
            {
            pushFollow(FOLLOW_identifier_in_prefix6179);
            identifier176=identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier176.getTree());
            COLON177=(Token)match(input,COLON,FOLLOW_COLON_in_prefix6181); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON177);



            // AST REWRITE
            // elements: identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 791:17: -> ^( PREFIX identifier )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:792:25: ^( PREFIX identifier )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREFIX, "PREFIX"), root_1);

                adaptor.addChild(root_1, stream_identifier.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "prefix"

    public static class uri_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "uri"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:795:1: uri : URI -> ^( NAME_SPACE URI ) ;
    public final FTSParser.uri_return uri() throws RecognitionException {
        FTSParser.uri_return retval = new FTSParser.uri_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token URI178=null;

        Object URI178_tree=null;
        RewriteRuleTokenStream stream_URI=new RewriteRuleTokenStream(adaptor,"token URI");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:796:9: ( URI -> ^( NAME_SPACE URI ) )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:797:9: URI
            {
            URI178=(Token)match(input,URI,FOLLOW_URI_in_uri6262); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_URI.add(URI178);



            // AST REWRITE
            // elements: URI
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 798:17: -> ^( NAME_SPACE URI )
            {
                // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:799:25: ^( NAME_SPACE URI )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NAME_SPACE, "NAME_SPACE"), root_1);

                adaptor.addChild(root_1, stream_URI.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch(RecognitionException e)
        {
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "uri"

    public static class identifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "identifier"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:802:1: identifier : ( ID -> ID | id1= ID DOT id2= ID ->);
    public final FTSParser.identifier_return identifier() throws RecognitionException {
        FTSParser.identifier_return retval = new FTSParser.identifier_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id1=null;
        Token id2=null;
        Token ID179=null;
        Token DOT180=null;

        Object id1_tree=null;
        Object id2_tree=null;
        Object ID179_tree=null;
        Object DOT180_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:803:9: ( ID -> ID | id1= ID DOT id2= ID ->)
            int alt61=2;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:804:9: ID
                    {
                    ID179=(Token)match(input,ID,FOLLOW_ID_in_identifier6343); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID179);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 805:17: -> ID
                    {
                        adaptor.addChild(root_0, stream_ID.nextNode());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:807:11: id1= ID DOT id2= ID
                    {
                    id1=(Token)match(input,ID,FOLLOW_ID_in_identifier6401); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id1);

                    DOT180=(Token)match(input,DOT,FOLLOW_DOT_in_identifier6403); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT180);

                    id2=(Token)match(input,ID,FOLLOW_ID_in_identifier6407); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id2);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 808:17: ->
                    {
                        adaptor.addChild(root_0, new CommonTree(new CommonToken(FTSLexer.ID, (id1!=null?id1.getText():null)+(DOT180!=null?DOT180.getText():null)+(id2!=null?id2.getText():null))));

                    }

                    retval.tree = root_0;}
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "identifier"

    public static class ftsWord_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsWord"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:811:1: ftsWord : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK );
    public final FTSParser.ftsWord_return ftsWord() throws RecognitionException {
        FTSParser.ftsWord_return retval = new FTSParser.ftsWord_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set181=null;

        Object set181_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:812:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            root_0 = (Object)adaptor.nil();

            set181=(Token)input.LT(1);
            if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||(input.LA(1)>=ID && input.LA(1)<=STAR)||input.LA(1)==QUESTION_MARK ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set181));
                state.errorRecovery=false;state.failed=false;
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsWord"

    public static class number_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "number"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:825:1: number : ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL );
    public final FTSParser.number_return number() throws RecognitionException {
        FTSParser.number_return retval = new FTSParser.number_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set182=null;

        Object set182_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:826:9: ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            root_0 = (Object)adaptor.nil();

            set182=(Token)input.LT(1);
            if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set182));
                state.errorRecovery=false;state.failed=false;
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "number"

    public static class ftsRangeWord_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ftsRangeWord"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:831:1: ftsRangeWord : ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL );
    public final FTSParser.ftsRangeWord_return ftsRangeWord() throws RecognitionException {
        FTSParser.ftsRangeWord_return retval = new FTSParser.ftsRangeWord_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set183=null;

        Object set183_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:832:9: ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            root_0 = (Object)adaptor.nil();

            set183=(Token)input.LT(1);
            if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||(input.LA(1)>=FTSPHRASE && input.LA(1)<=FTSWILD)||input.LA(1)==FLOATING_POINT_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set183));
                state.errorRecovery=false;state.failed=false;
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ftsRangeWord"

    public static class or_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "or"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:844:1: or : ( OR | BAR BAR );
    public final FTSParser.or_return or() throws RecognitionException {
        FTSParser.or_return retval = new FTSParser.or_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR184=null;
        Token BAR185=null;
        Token BAR186=null;

        Object OR184_tree=null;
        Object BAR185_tree=null;
        Object BAR186_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:845:9: ( OR | BAR BAR )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==OR) ) {
                alt62=1;
            }
            else if ( (LA62_0==BAR) ) {
                alt62=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:846:9: OR
                    {
                    root_0 = (Object)adaptor.nil();

                    OR184=(Token)match(input,OR,FOLLOW_OR_in_or6758); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OR184_tree = (Object)adaptor.create(OR184);
                    adaptor.addChild(root_0, OR184_tree);
                    }

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:847:11: BAR BAR
                    {
                    root_0 = (Object)adaptor.nil();

                    BAR185=(Token)match(input,BAR,FOLLOW_BAR_in_or6770); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BAR185_tree = (Object)adaptor.create(BAR185);
                    adaptor.addChild(root_0, BAR185_tree);
                    }
                    BAR186=(Token)match(input,BAR,FOLLOW_BAR_in_or6772); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BAR186_tree = (Object)adaptor.create(BAR186);
                    adaptor.addChild(root_0, BAR186_tree);
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "or"

    public static class and_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "and"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:850:1: and : ( AND | AMP AMP );
    public final FTSParser.and_return and() throws RecognitionException {
        FTSParser.and_return retval = new FTSParser.and_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AND187=null;
        Token AMP188=null;
        Token AMP189=null;

        Object AND187_tree=null;
        Object AMP188_tree=null;
        Object AMP189_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:851:9: ( AND | AMP AMP )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==AND) ) {
                alt63=1;
            }
            else if ( (LA63_0==AMP) ) {
                alt63=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:852:9: AND
                    {
                    root_0 = (Object)adaptor.nil();

                    AND187=(Token)match(input,AND,FOLLOW_AND_in_and6805); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AND187_tree = (Object)adaptor.create(AND187);
                    adaptor.addChild(root_0, AND187_tree);
                    }

                    }
                    break;
                case 2 :
                    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:853:11: AMP AMP
                    {
                    root_0 = (Object)adaptor.nil();

                    AMP188=(Token)match(input,AMP,FOLLOW_AMP_in_and6817); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AMP188_tree = (Object)adaptor.create(AMP188);
                    adaptor.addChild(root_0, AMP188_tree);
                    }
                    AMP189=(Token)match(input,AMP,FOLLOW_AMP_in_and6819); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AMP189_tree = (Object)adaptor.create(AMP189);
                    adaptor.addChild(root_0, AMP189_tree);
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "and"

    public static class not_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not"
    // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:856:1: not : ( NOT | EXCLAMATION );
    public final FTSParser.not_return not() throws RecognitionException {
        FTSParser.not_return retval = new FTSParser.not_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set190=null;

        Object set190_tree=null;

        try {
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:857:9: ( NOT | EXCLAMATION )
            // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
            {
            root_0 = (Object)adaptor.nil();

            set190=(Token)input.LT(1);
            if ( input.LA(1)==NOT||input.LA(1)==EXCLAMATION ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set190));
                state.errorRecovery=false;state.failed=false;
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
           throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "not"

    // $ANTLR start synpred1_FTS
    public final void synpred1_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:9: ( not )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:10: not
        {
        pushFollow(FOLLOW_not_in_synpred1_FTS1216);
        not();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_FTS

    // $ANTLR start synpred2_FTS
    public final void synpred2_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:457:9: ( ftsFieldGroupProximity )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:457:10: ftsFieldGroupProximity
        {
        pushFollow(FOLLOW_ftsFieldGroupProximity_in_synpred2_FTS1727);
        ftsFieldGroupProximity();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_FTS

    // $ANTLR start synpred3_FTS
    public final void synpred3_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:21: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:22: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred3_FTS1797);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_FTS

    // $ANTLR start synpred4_FTS
    public final void synpred4_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:26: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:27: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred4_FTS1872);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_FTS

    // $ANTLR start synpred5_FTS
    public final void synpred5_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:466:23: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:466:24: slop
        {
        pushFollow(FOLLOW_slop_in_synpred5_FTS1947);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_FTS

    // $ANTLR start synpred6_FTS
    public final void synpred6_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:28: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:29: slop
        {
        pushFollow(FOLLOW_slop_in_synpred6_FTS2022);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_FTS

    // $ANTLR start synpred7_FTS
    public final void synpred7_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:32: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:33: slop
        {
        pushFollow(FOLLOW_slop_in_synpred7_FTS2097);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_FTS

    // $ANTLR start synpred8_FTS
    public final void synpred8_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:24: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:25: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred8_FTS2172);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_FTS

    // $ANTLR start synpred9_FTS
    public final void synpred9_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:9: ( not )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:10: not
        {
        pushFollow(FOLLOW_not_in_synpred9_FTS4001);
        not();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_FTS

    // $ANTLR start synpred10_FTS
    public final void synpred10_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:9: ( ftsFieldGroupProximity )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:10: ftsFieldGroupProximity
        {
        pushFollow(FOLLOW_ftsFieldGroupProximity_in_synpred10_FTS4366);
        ftsFieldGroupProximity();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_FTS

    // $ANTLR start synpred11_FTS
    public final void synpred11_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:31: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:32: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred11_FTS4436);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_FTS

    // $ANTLR start synpred12_FTS
    public final void synpred12_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:36: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:37: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred12_FTS4511);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_FTS

    // $ANTLR start synpred13_FTS
    public final void synpred13_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:33: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:34: slop
        {
        pushFollow(FOLLOW_slop_in_synpred13_FTS4586);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_FTS

    // $ANTLR start synpred14_FTS
    public final void synpred14_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:38: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:39: slop
        {
        pushFollow(FOLLOW_slop_in_synpred14_FTS4661);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_FTS

    // $ANTLR start synpred15_FTS
    public final void synpred15_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:42: ( slop )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:43: slop
        {
        pushFollow(FOLLOW_slop_in_synpred15_FTS4736);
        slop();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_FTS

    // $ANTLR start synpred16_FTS
    public final void synpred16_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:34: ( fuzzy )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:35: fuzzy
        {
        pushFollow(FOLLOW_fuzzy_in_synpred16_FTS4811);
        fuzzy();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_FTS

    // $ANTLR start synpred17_FTS
    public final void synpred17_FTS_fragment() throws RecognitionException {   
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:38: ( proximityGroup )
        // W:\\alfresco\\HEAD\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:39: proximityGroup
        {
        pushFollow(FOLLOW_proximityGroup_in_synpred17_FTS5305);
        proximityGroup();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_FTS

    // Delegated rules

    public final boolean synpred17_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_FTS() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_FTS_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA50 dfa50 = new DFA50(this);
    protected DFA44 dfa44 = new DFA44(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA48 dfa48 = new DFA48(this);
    protected DFA49 dfa49 = new DFA49(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA58 dfa58 = new DFA58(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA61 dfa61 = new DFA61(this);
    static final String DFA1_eotS =
        "\27\uffff";
    static final String DFA1_eofS =
        "\27\uffff";
    static final String DFA1_minS =
        "\1\53\3\0\2\uffff\17\0\2\uffff";
    static final String DFA1_maxS =
        "\1\114\3\0\2\uffff\17\0\2\uffff";
    static final String DFA1_acceptS =
        "\4\uffff\2\2\17\uffff\1\3\1\1";
    static final String DFA1_specialS =
        "\1\0\1\1\1\2\1\3\2\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\2\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\23\1\24\1\3\1\21\1\uffff\1\22\1\uffff\1\15\1\12\2\uffff"+
            "\1\14\1\2\1\6\3\12\1\1\1\16\1\12\1\10\1\uffff\1\17\1\20\2\uffff"+
            "\1\11\1\13\1\uffff\1\10\1\25\1\4\1\5\1\7",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "362:1: ftsDisjunction : ({...}? => cmisExplicitDisjunction | {...}? => ftsExplicitDisjunction | {...}? => ftsImplicitDisjunction );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_0 = input.LA(1);

                         
                        int index1_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_0==NOT) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 1;}

                        else if ( (LA1_0==FTSPHRASE) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 2;}

                        else if ( (LA1_0==MINUS) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 3;}

                        else if ( (LA1_0==AND) && ((getMode() == Mode.DEFAULT_CONJUNCTION))) {s = 4;}

                        else if ( (LA1_0==AMP) && ((getMode() == Mode.DEFAULT_CONJUNCTION))) {s = 5;}

                        else if ( (LA1_0==ID) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 6;}

                        else if ( (LA1_0==EXCLAMATION) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 7;}

                        else if ( (LA1_0==STAR||LA1_0==QUESTION_MARK) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 8;}

                        else if ( (LA1_0==AT) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 9;}

                        else if ( (LA1_0==DECIMAL_INTEGER_LITERAL||(LA1_0>=FTSWORD && LA1_0<=FTSWILD)||LA1_0==FLOATING_POINT_LITERAL) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 10;}

                        else if ( (LA1_0==URI) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 11;}

                        else if ( (LA1_0==EQUALS) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 12;}

                        else if ( (LA1_0==TILDA) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 13;}

                        else if ( (LA1_0==TO) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.CMIS)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 14;}

                        else if ( (LA1_0==LSQUARE) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 15;}

                        else if ( (LA1_0==LT) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 16;}

                        else if ( (LA1_0==LPAREN) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 17;}

                        else if ( (LA1_0==PERCENT) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 18;}

                        else if ( (LA1_0==PLUS) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 19;}

                        else if ( (LA1_0==BAR) && (((getMode() == Mode.DEFAULT_DISJUNCTION)||(getMode() == Mode.DEFAULT_CONJUNCTION)))) {s = 20;}

                        else if ( (LA1_0==OR) && ((getMode() == Mode.DEFAULT_DISJUNCTION))) {s = 21;}

                         
                        input.seek(index1_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_1 = input.LA(1);

                         
                        int index1_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_2 = input.LA(1);

                         
                        int index1_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_3 = input.LA(1);

                         
                        int index1_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_6 = input.LA(1);

                         
                        int index1_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA1_7 = input.LA(1);

                         
                        int index1_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA1_8 = input.LA(1);

                         
                        int index1_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA1_11 = input.LA(1);

                         
                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA1_12 = input.LA(1);

                         
                        int index1_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA1_13 = input.LA(1);

                         
                        int index1_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA1_14 = input.LA(1);

                         
                        int index1_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.CMIS)) ) {s = 22;}

                        else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_14);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA1_15 = input.LA(1);

                         
                        int index1_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_15);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA1_16 = input.LA(1);

                         
                        int index1_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_16);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA1_17 = input.LA(1);

                         
                        int index1_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_17);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA1_18 = input.LA(1);

                         
                        int index1_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_18);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA1_19 = input.LA(1);

                         
                        int index1_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_19);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA1_20 = input.LA(1);

                         
                        int index1_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {s = 5;}

                        else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {s = 21;}

                         
                        input.seek(index1_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA5_eotS =
        "\26\uffff";
    static final String DFA5_eofS =
        "\1\1\25\uffff";
    static final String DFA5_minS =
        "\1\53\25\uffff";
    static final String DFA5_maxS =
        "\1\114\25\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\2\2\23\1";
    static final String DFA5_specialS =
        "\26\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\24\1\4\1\25\1\22\1\2\1\23\1\uffff\1\16\1\11\2\uffff\1\14"+
            "\1\15\1\7\3\11\1\5\1\17\1\11\1\13\1\uffff\1\20\1\21\2\uffff"+
            "\1\10\1\12\1\uffff\1\13\1\3\2\uffff\1\6",
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

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "()+ loopback of 385:9: ( ( or )? ftsExplicitConjunction )+";
        }
    }
    static final String DFA4_eotS =
        "\42\uffff";
    static final String DFA4_eofS =
        "\42\uffff";
    static final String DFA4_minS =
        "\1\53\1\uffff\1\54\37\uffff";
    static final String DFA4_maxS =
        "\1\114\1\uffff\1\110\37\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\1\1\uffff\21\2\1\1\15\2";
    static final String DFA4_specialS =
        "\42\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\22\1\2\1\23\1\20\1\uffff\1\21\1\uffff\1\14\1\7\2\uffff\1"+
            "\12\1\13\1\5\3\7\1\3\1\15\1\7\1\11\1\uffff\1\16\1\17\2\uffff"+
            "\1\6\1\10\1\uffff\1\11\1\1\2\uffff\1\4",
            "",
            "\1\24\1\uffff\1\40\1\uffff\1\41\1\uffff\1\34\1\27\2\uffff"+
            "\1\32\1\33\1\25\3\27\2\35\1\27\1\31\1\uffff\1\36\1\37\2\uffff"+
            "\1\26\1\30\1\uffff\1\31",
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
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "385:10: ( or )?";
        }
    }
    static final String DFA8_eotS =
        "\46\uffff";
    static final String DFA8_eofS =
        "\1\3\45\uffff";
    static final String DFA8_minS =
        "\1\53\1\uffff\1\54\43\uffff";
    static final String DFA8_maxS =
        "\1\114\1\uffff\1\110\43\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\2\1\uffff\2\2\23\1\1\2\15\1";
    static final String DFA8_specialS =
        "\46\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\26\1\2\1\27\1\24\1\4\1\25\1\uffff\1\20\1\13\2\uffff\1\16"+
            "\1\17\1\11\3\13\1\7\1\21\1\13\1\15\1\uffff\1\22\1\23\2\uffff"+
            "\1\12\1\14\1\uffff\1\15\1\1\1\5\1\6\1\10",
            "",
            "\1\30\1\uffff\1\44\1\uffff\1\45\1\uffff\1\40\1\33\2\uffff"+
            "\1\36\1\37\1\31\3\33\2\41\1\33\1\35\1\uffff\1\42\1\43\2\uffff"+
            "\1\32\1\34\1\uffff\1\35",
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
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "()+ loopback of 403:9: ( ( and )? ftsPrefixed )+";
        }
    }
    static final String DFA7_eotS =
        "\25\uffff";
    static final String DFA7_eofS =
        "\25\uffff";
    static final String DFA7_minS =
        "\1\53\24\uffff";
    static final String DFA7_maxS =
        "\1\114\24\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\2\1\22\2";
    static final String DFA7_specialS =
        "\25\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\22\1\23\1\24\1\20\1\uffff\1\21\1\uffff\1\14\1\7\2\uffff"+
            "\1\12\1\13\1\5\3\7\1\3\1\15\1\7\1\11\1\uffff\1\16\1\17\2\uffff"+
            "\1\6\1\10\1\uffff\1\11\1\uffff\1\1\1\2\1\4",
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

    class DFA7 extends DFA {

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
        public String getDescription() {
            return "403:10: ( and )?";
        }
    }
    static final String DFA15_eotS =
        "\23\uffff";
    static final String DFA15_eofS =
        "\23\uffff";
    static final String DFA15_minS =
        "\1\53\1\0\21\uffff";
    static final String DFA15_maxS =
        "\1\114\1\0\21\uffff";
    static final String DFA15_acceptS =
        "\2\uffff\1\1\15\2\1\3\1\4\1\5";
    static final String DFA15_specialS =
        "\1\0\1\1\21\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\20\1\21\1\22\1\16\1\uffff\1\17\1\uffff\1\12\1\5\2\uffff"+
            "\1\10\1\11\1\3\3\5\1\1\1\13\1\5\1\7\1\uffff\1\14\1\15\2\uffff"+
            "\1\4\1\6\1\uffff\1\7\3\uffff\1\2",
            "\1\uffff",
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

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "422:1: ftsPrefixed : ( ( not )=> not ftsTest ( boost )? -> ^( NEGATION ftsTest ( boost )? ) | ftsTest ( boost )? -> ^( DEFAULT ftsTest ( boost )? ) | PLUS ftsTest ( boost )? -> ^( MANDATORY ftsTest ( boost )? ) | BAR ftsTest ( boost )? -> ^( OPTIONAL ftsTest ( boost )? ) | MINUS ftsTest ( boost )? -> ^( EXCLUDE ftsTest ( boost )? ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA15_0 = input.LA(1);

                         
                        int index15_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA15_0==NOT) ) {s = 1;}

                        else if ( (LA15_0==EXCLAMATION) && (synpred1_FTS())) {s = 2;}

                        else if ( (LA15_0==ID) ) {s = 3;}

                        else if ( (LA15_0==AT) ) {s = 4;}

                        else if ( (LA15_0==DECIMAL_INTEGER_LITERAL||(LA15_0>=FTSWORD && LA15_0<=FTSWILD)||LA15_0==FLOATING_POINT_LITERAL) ) {s = 5;}

                        else if ( (LA15_0==URI) ) {s = 6;}

                        else if ( (LA15_0==STAR||LA15_0==QUESTION_MARK) ) {s = 7;}

                        else if ( (LA15_0==EQUALS) ) {s = 8;}

                        else if ( (LA15_0==FTSPHRASE) ) {s = 9;}

                        else if ( (LA15_0==TILDA) ) {s = 10;}

                        else if ( (LA15_0==TO) ) {s = 11;}

                        else if ( (LA15_0==LSQUARE) ) {s = 12;}

                        else if ( (LA15_0==LT) ) {s = 13;}

                        else if ( (LA15_0==LPAREN) ) {s = 14;}

                        else if ( (LA15_0==PERCENT) ) {s = 15;}

                        else if ( (LA15_0==PLUS) ) {s = 16;}

                        else if ( (LA15_0==BAR) ) {s = 17;}

                        else if ( (LA15_0==MINUS) ) {s = 18;}

                         
                        input.seek(index15_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA15_1 = input.LA(1);

                         
                        int index15_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_FTS()) ) {s = 2;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index15_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 15, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA23_eotS =
        "\u00c1\uffff";
    static final String DFA23_eofS =
        "\1\uffff\1\25\1\uffff\1\25\3\uffff\1\65\1\uffff\1\25\5\uffff\1"+
        "\25\35\uffff\1\144\34\uffff\1\u0080\16\uffff\2\25\123\uffff\1\144"+
        "\3\uffff\1\u0080\2\uffff\1\25\13\uffff";
    static final String DFA23_minS =
        "\1\56\1\53\1\70\1\53\1\70\1\uffff\1\63\1\53\1\63\1\53\4\uffff\1"+
        "\70\1\53\1\56\31\uffff\2\65\1\70\1\53\1\70\32\uffff\1\70\1\53\1"+
        "\70\2\uffff\1\65\1\53\1\0\4\uffff\3\0\1\uffff\2\53\1\70\1\56\1\65"+
        "\1\70\1\63\30\uffff\2\65\1\70\1\63\30\uffff\1\65\1\53\26\uffff\2"+
        "\65\1\53\1\70\1\63\1\65\1\53\1\70\1\63\1\53\10\uffff\2\65\1\uffff";
    static final String DFA23_maxS =
        "\1\110\1\114\1\106\1\114\1\70\1\uffff\1\110\1\114\1\110\1\114\4"+
        "\uffff\1\70\1\114\1\110\31\uffff\2\107\1\106\1\114\1\70\32\uffff"+
        "\1\106\1\114\1\70\2\uffff\1\65\1\114\1\0\4\uffff\3\0\1\uffff\2\114"+
        "\1\70\1\110\1\107\1\70\1\110\30\uffff\2\107\1\70\1\110\30\uffff"+
        "\1\107\1\114\26\uffff\2\65\1\114\1\70\1\110\1\65\1\114\1\70\1\110"+
        "\1\114\10\uffff\2\65\1\uffff";
    static final String DFA23_acceptS =
        "\5\uffff\1\2\4\uffff\2\10\1\12\1\13\3\uffff\30\2\1\10\5\uffff\1"+
        "\3\1\5\30\4\3\uffff\1\6\1\7\3\uffff\4\2\3\uffff\1\11\7\uffff\30"+
        "\3\4\uffff\30\7\2\uffff\1\1\25\2\12\uffff\10\2\2\uffff\1\2";
    static final String DFA23_specialS =
        "\117\uffff\1\0\1\3\4\uffff\1\1\1\4\1\2\151\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\14\1\uffff\1\15\1\uffff\1\10\1\3\2\uffff\1\6\1\7\1\1\3\3"+
            "\2\11\1\3\1\5\1\uffff\1\12\1\13\2\uffff\1\2\1\4\1\uffff\1\5",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\21\1\35\1\22\1\20"+
            "\1\40\1\41\1\33\3\35\1\31\1\42\1\35\1\17\1\51\1\43\1\44\2\uffff"+
            "\1\34\1\36\1\16\1\37\1\27\1\23\1\24\1\32",
            "\1\52\15\uffff\1\4",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\21\1\35\1\22\1\uffff"+
            "\1\40\1\41\1\33\3\35\1\31\1\42\1\35\1\17\1\51\1\43\1\44\2\uffff"+
            "\1\34\1\36\1\uffff\1\37\1\27\1\23\1\24\1\32",
            "\1\53",
            "",
            "\1\57\3\uffff\1\60\1\55\7\57\5\uffff\1\54\1\56\1\uffff\1\57",
            "\1\107\1\70\1\110\1\105\1\66\1\106\1\uffff\1\61\1\75\1\62"+
            "\1\uffff\1\100\1\101\1\73\3\75\1\71\1\102\1\75\1\77\1\51\1\103"+
            "\1\104\2\uffff\1\74\1\76\1\uffff\1\77\1\67\1\63\1\64\1\72",
            "\1\115\3\uffff\1\114\1\112\7\115\5\uffff\1\111\1\113\1\uffff"+
            "\1\115",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\21\1\35\1\22\1\uffff"+
            "\1\40\1\41\1\33\3\35\1\31\1\42\1\35\1\17\1\uffff\1\43\1\44\2"+
            "\uffff\1\34\1\36\1\uffff\1\37\1\27\1\23\1\24\1\32",
            "",
            "",
            "",
            "",
            "\1\116",
            "\1\47\1\30\1\50\1\117\1\26\1\46\1\uffff\1\121\1\126\1\122"+
            "\1\uffff\1\40\1\41\1\125\3\126\1\120\1\127\1\126\1\37\1\uffff"+
            "\1\43\1\44\2\uffff\1\34\1\36\1\uffff\1\37\1\27\1\123\1\124\1"+
            "\32",
            "\1\130\4\uffff\1\132\3\uffff\1\7\1\131\3\132\2\5\1\132\1\5"+
            "\1\uffff\1\12\1\13\5\uffff\1\5",
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
            "\1\20\21\uffff\1\16",
            "\1\134\21\uffff\1\133",
            "\1\135\15\uffff\1\56",
            "\1\166\1\147\1\167\1\164\1\145\1\165\1\uffff\1\140\1\154\1"+
            "\141\1\137\1\157\1\160\1\152\3\154\1\150\1\161\1\154\1\156\1"+
            "\uffff\1\162\1\163\2\uffff\1\153\1\155\1\136\1\156\1\146\1\142"+
            "\1\143\1\151",
            "\1\170",
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
            "\1\171\15\uffff\1\113",
            "\1\u0092\1\u0083\1\u0093\1\u0090\1\u0081\1\u0091\1\uffff\1"+
            "\174\1\u0088\1\175\1\173\1\u008b\1\u008c\1\u0086\3\u0088\1\u0084"+
            "\1\u008d\1\u0088\1\u008a\1\uffff\1\u008e\1\u008f\2\uffff\1\u0087"+
            "\1\u0089\1\172\1\u008a\1\u0082\1\176\1\177\1\u0085",
            "\1\u0094",
            "",
            "",
            "\1\20",
            "\1\u00a9\1\u00aa\1\u0099\1\u00a7\1\u0096\1\u00a8\1\uffff\1"+
            "\u00a3\1\u0095\2\uffff\1\u00a2\1\u0098\1\u009c\3\u00a0\1\u0097"+
            "\1\u00a4\1\u00a0\1\u009e\1\uffff\1\u00a5\1\u00a6\2\uffff\1\u009f"+
            "\1\u00a1\1\uffff\1\u009e\1\u00ab\1\u009a\1\u009b\1\u009d",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\21\1\35\1\22\1\134"+
            "\1\40\1\41\1\33\3\35\1\31\1\42\1\35\1\37\1\51\1\43\1\44\2\uffff"+
            "\1\34\1\36\1\133\1\37\1\27\1\23\1\24\1\32",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\21\1\35\1\22\1\uffff"+
            "\1\40\1\41\1\33\3\35\1\31\1\42\1\35\1\37\1\51\1\43\1\44\2\uffff"+
            "\1\34\1\36\1\uffff\1\37\1\27\1\23\1\24\1\32",
            "\1\u00ac",
            "\1\130\4\uffff\1\132\3\uffff\1\7\4\132\2\5\1\132\1\5\1\uffff"+
            "\1\12\1\13\5\uffff\1\5",
            "\1\137\21\uffff\1\136",
            "\1\u00ad",
            "\1\57\3\uffff\1\60\1\u00ae\7\57\10\uffff\1\57",
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
            "\1\u00b0\21\uffff\1\u00af",
            "\1\173\21\uffff\1\172",
            "\1\u00b1",
            "\1\115\3\uffff\1\114\1\u00b2\7\115\10\uffff\1\115",
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
            "\1\u00b4\21\uffff\1\u00b3",
            "\1\u00a9\1\u00b7\1\u0099\1\u00a7\1\u00b5\1\u00a8\1\uffff\1"+
            "\u00b9\1\u00a0\1\u00ba\1\uffff\1\u00a2\1\u0098\1\u009c\3\u00a0"+
            "\1\u0097\1\u00a4\1\u00a0\1\u00b8\1\u00bd\1\u00a5\1\u00a6\2\uffff"+
            "\1\u009f\1\u00a1\1\uffff\1\u009e\1\u00b6\1\u00bb\1\u00bc\1\u009d",
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
            "\1\134",
            "\1\137",
            "\1\166\1\147\1\167\1\164\1\145\1\165\1\uffff\1\140\1\154\1"+
            "\141\1\u00b0\1\157\1\160\1\152\3\154\1\150\1\161\1\154\1\156"+
            "\1\uffff\1\162\1\163\2\uffff\1\153\1\155\1\u00af\1\156\1\146"+
            "\1\142\1\143\1\151",
            "\1\u00be",
            "\1\57\3\uffff\1\60\10\57\10\uffff\1\57",
            "\1\173",
            "\1\u0092\1\u0083\1\u0093\1\u0090\1\u0081\1\u0091\1\uffff\1"+
            "\174\1\u0088\1\175\1\u00b4\1\u008b\1\u008c\1\u0086\3\u0088\1"+
            "\u0084\1\u008d\1\u0088\1\u008a\1\uffff\1\u008e\1\u008f\2\uffff"+
            "\1\u0087\1\u0089\1\u00b3\1\u008a\1\u0082\1\176\1\177\1\u0085",
            "\1\u00bf",
            "\1\115\3\uffff\1\114\10\115\10\uffff\1\115",
            "\1\47\1\30\1\50\1\45\1\26\1\46\1\uffff\1\u00c0\1\126\1\122"+
            "\1\uffff\1\40\1\41\1\125\3\126\1\120\1\127\1\126\1\37\1\uffff"+
            "\1\43\1\44\2\uffff\1\34\1\36\1\uffff\1\37\1\27\1\123\1\124\1"+
            "\32",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00b0",
            "\1\u00b4",
            ""
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "455:1: ftsTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ftsTerm ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsTerm ( fuzzy )? ) | ftsExactTerm ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsExactTerm ( fuzzy )? ) | ftsPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsPhrase ( slop )? ) | ftsExactPhrase ( ( slop )=> slop )? -> ^( EXACT_PHRASE ftsExactPhrase ( slop )? ) | ftsTokenisedPhrase ( ( slop )=> slop )? -> ^( PHRASE ftsTokenisedPhrase ( slop )? ) | ftsSynonym ( ( fuzzy )=> fuzzy )? -> ^( SYNONYM ftsSynonym ( fuzzy )? ) | ftsRange -> ^( RANGE ftsRange ) | ftsFieldGroup -> ftsFieldGroup | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA23_79 = input.LA(1);

                         
                        int index23_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA23_79==DECIMAL_INTEGER_LITERAL) ) {s = 149;}

                        else if ( (LA23_79==RPAREN) && (synpred2_FTS())) {s = 150;}

                        else if ( (LA23_79==NOT) ) {s = 151;}

                        else if ( (LA23_79==FTSPHRASE) ) {s = 152;}

                        else if ( (LA23_79==MINUS) ) {s = 153;}

                        else if ( (LA23_79==AND) ) {s = 154;}

                        else if ( (LA23_79==AMP) ) {s = 155;}

                        else if ( (LA23_79==ID) ) {s = 156;}

                        else if ( (LA23_79==EXCLAMATION) ) {s = 157;}

                        else if ( (LA23_79==STAR||LA23_79==QUESTION_MARK) ) {s = 158;}

                        else if ( (LA23_79==AT) ) {s = 159;}

                        else if ( ((LA23_79>=FTSWORD && LA23_79<=FTSWILD)||LA23_79==FLOATING_POINT_LITERAL) ) {s = 160;}

                        else if ( (LA23_79==URI) ) {s = 161;}

                        else if ( (LA23_79==EQUALS) ) {s = 162;}

                        else if ( (LA23_79==TILDA) ) {s = 163;}

                        else if ( (LA23_79==TO) ) {s = 164;}

                        else if ( (LA23_79==LSQUARE) ) {s = 165;}

                        else if ( (LA23_79==LT) ) {s = 166;}

                        else if ( (LA23_79==LPAREN) ) {s = 167;}

                        else if ( (LA23_79==PERCENT) ) {s = 168;}

                        else if ( (LA23_79==PLUS) ) {s = 169;}

                        else if ( (LA23_79==BAR) ) {s = 170;}

                        else if ( (LA23_79==OR) ) {s = 171;}

                         
                        input.seek(index23_79);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA23_85 = input.LA(1);

                         
                        int index23_85 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_FTS()) ) {s = 150;}

                        else if ( (true) ) {s = 171;}

                         
                        input.seek(index23_85);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA23_87 = input.LA(1);

                         
                        int index23_87 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_FTS()) ) {s = 150;}

                        else if ( (true) ) {s = 171;}

                         
                        input.seek(index23_87);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA23_80 = input.LA(1);

                         
                        int index23_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_FTS()) ) {s = 150;}

                        else if ( (true) ) {s = 171;}

                         
                        input.seek(index23_80);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA23_86 = input.LA(1);

                         
                        int index23_86 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_FTS()) ) {s = 150;}

                        else if ( (true) ) {s = 171;}

                         
                        input.seek(index23_86);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 23, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA17_eotS =
        "\40\uffff";
    static final String DFA17_eofS =
        "\1\5\37\uffff";
    static final String DFA17_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA17_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA17_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA17_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\5\36\1\31\1\36\5\uffff\1\32\1\34\1"+
            "\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "460:19: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_25 = input.LA(1);

                         
                        int index17_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index17_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA18_eotS =
        "\40\uffff";
    static final String DFA18_eofS =
        "\1\5\37\uffff";
    static final String DFA18_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA18_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA18_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\5\36\1\31\1\36\5\uffff\1\32\1\34\1"+
            "\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "463:24: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA18_25 = input.LA(1);

                         
                        int index18_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index18_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 18, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA19_eotS =
        "\40\uffff";
    static final String DFA19_eofS =
        "\1\5\37\uffff";
    static final String DFA19_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA19_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA19_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\7\36\5\uffff\1\32\1\34\1\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "466:21: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_25 = input.LA(1);

                         
                        int index19_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index19_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA20_eotS =
        "\40\uffff";
    static final String DFA20_eofS =
        "\1\5\37\uffff";
    static final String DFA20_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA20_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA20_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA20_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\7\36\5\uffff\1\32\1\34\1\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "469:26: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA20_25 = input.LA(1);

                         
                        int index20_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index20_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 20, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA21_eotS =
        "\40\uffff";
    static final String DFA21_eofS =
        "\1\5\37\uffff";
    static final String DFA21_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA21_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA21_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA21_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\7\36\5\uffff\1\32\1\34\1\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "472:30: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_25 = input.LA(1);

                         
                        int index21_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index21_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 21, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA22_eotS =
        "\40\uffff";
    static final String DFA22_eofS =
        "\1\5\37\uffff";
    static final String DFA22_minS =
        "\1\53\1\63\27\uffff\1\0\6\uffff";
    static final String DFA22_maxS =
        "\1\114\1\110\27\uffff\1\0\6\uffff";
    static final String DFA22_acceptS =
        "\2\uffff\27\2\1\uffff\5\2\1\1";
    static final String DFA22_specialS =
        "\31\uffff\1\0\6\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\27\1\10\1\30\1\25\1\6\1\26\1\uffff\1\1\1\15\1\2\1\uffff"+
            "\1\20\1\21\1\13\3\15\1\11\1\22\1\15\1\17\1\uffff\1\23\1\24\2"+
            "\uffff\1\14\1\16\1\uffff\1\17\1\7\1\3\1\4\1\12",
            "\1\31\3\uffff\1\35\1\33\5\36\1\31\1\36\5\uffff\1\32\1\34\1"+
            "\uffff\1\36",
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
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "475:22: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_25 = input.LA(1);

                         
                        int index22_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_FTS()) ) {s = 31;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index22_25);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA28_eotS =
        "\37\uffff";
    static final String DFA28_eofS =
        "\2\uffff\1\13\34\uffff";
    static final String DFA28_minS =
        "\1\63\1\uffff\1\53\34\uffff";
    static final String DFA28_maxS =
        "\1\110\1\uffff\1\114\34\uffff";
    static final String DFA28_acceptS =
        "\1\uffff\1\1\1\uffff\1\1\1\2\2\1\30\2";
    static final String DFA28_specialS =
        "\37\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\4\4\uffff\1\2\7\4\5\uffff\1\1\1\3\1\uffff\1\4",
            "",
            "\1\35\1\16\1\36\1\33\1\14\1\34\1\uffff\1\7\1\23\1\10\1\6\1"+
            "\26\1\27\1\21\3\23\1\17\1\30\1\23\1\25\1\uffff\1\31\1\32\2\uffff"+
            "\1\22\1\24\1\5\1\25\1\15\1\11\1\12\1\20",
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
            ""
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

    class DFA28 extends DFA {

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
        public String getDescription() {
            return "532:9: ( fieldReference COLON )?";
        }
    }
    static final String DFA30_eotS =
        "\12\uffff";
    static final String DFA30_eofS =
        "\12\uffff";
    static final String DFA30_minS =
        "\1\63\1\uffff\1\65\7\uffff";
    static final String DFA30_maxS =
        "\1\106\1\uffff\1\107\7\uffff";
    static final String DFA30_acceptS =
        "\1\uffff\1\1\1\uffff\1\1\3\2\2\1\1\2";
    static final String DFA30_specialS =
        "\12\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\4\3\uffff\1\4\1\2\3\4\2\uffff\1\4\2\uffff\1\5\1\6\2\uffff"+
            "\1\1\1\3",
            "",
            "\1\10\12\uffff\1\11\6\uffff\1\7",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "581:9: ( fieldReference COLON )?";
        }
    }
    static final String DFA31_eotS =
        "\22\uffff";
    static final String DFA31_eofS =
        "\22\uffff";
    static final String DFA31_minS =
        "\1\53\2\uffff\16\0\1\uffff";
    static final String DFA31_maxS =
        "\1\114\2\uffff\16\0\1\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\2\1\16\uffff\1\2";
    static final String DFA31_specialS =
        "\1\0\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\uffff}>";
    static final String[] DFA31_transitionS = {
            "\1\16\1\17\1\20\1\15\3\uffff\1\11\1\5\2\uffff\1\7\1\10\4\5"+
            "\1\3\1\12\1\5\1\6\1\uffff\1\13\1\14\5\uffff\1\6\1\21\1\1\1\2"+
            "\1\4",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "592:1: ftsFieldGroupDisjunction : ({...}? => ftsFieldGroupExplicitDisjunction | {...}? => ftsFieldGroupImplicitDisjunction );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_0 = input.LA(1);

                         
                        int index31_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA31_0==AND) && ((defaultFieldConjunction() == true))) {s = 1;}

                        else if ( (LA31_0==AMP) && ((defaultFieldConjunction() == true))) {s = 2;}

                        else if ( (LA31_0==NOT) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 3;}

                        else if ( (LA31_0==EXCLAMATION) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 4;}

                        else if ( (LA31_0==DECIMAL_INTEGER_LITERAL||(LA31_0>=ID && LA31_0<=FTSWILD)||LA31_0==FLOATING_POINT_LITERAL) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 5;}

                        else if ( (LA31_0==STAR||LA31_0==QUESTION_MARK) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 6;}

                        else if ( (LA31_0==EQUALS) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 7;}

                        else if ( (LA31_0==FTSPHRASE) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 8;}

                        else if ( (LA31_0==TILDA) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 9;}

                        else if ( (LA31_0==TO) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 10;}

                        else if ( (LA31_0==LSQUARE) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 11;}

                        else if ( (LA31_0==LT) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 12;}

                        else if ( (LA31_0==LPAREN) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 13;}

                        else if ( (LA31_0==PLUS) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 14;}

                        else if ( (LA31_0==BAR) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 15;}

                        else if ( (LA31_0==MINUS) && (((defaultFieldConjunction() == false)||(defaultFieldConjunction() == true)))) {s = 16;}

                        else if ( (LA31_0==OR) && ((defaultFieldConjunction() == false))) {s = 17;}

                         
                        input.seek(index31_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA31_3 = input.LA(1);

                         
                        int index31_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA31_4 = input.LA(1);

                         
                        int index31_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA31_5 = input.LA(1);

                         
                        int index31_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA31_6 = input.LA(1);

                         
                        int index31_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA31_7 = input.LA(1);

                         
                        int index31_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA31_8 = input.LA(1);

                         
                        int index31_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA31_9 = input.LA(1);

                         
                        int index31_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA31_10 = input.LA(1);

                         
                        int index31_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA31_11 = input.LA(1);

                         
                        int index31_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA31_12 = input.LA(1);

                         
                        int index31_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA31_13 = input.LA(1);

                         
                        int index31_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA31_14 = input.LA(1);

                         
                        int index31_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_14);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA31_15 = input.LA(1);

                         
                        int index31_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_15);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA31_16 = input.LA(1);

                         
                        int index31_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((defaultFieldConjunction() == true)) ) {s = 2;}

                        else if ( ((defaultFieldConjunction() == false)) ) {s = 17;}

                         
                        input.seek(index31_16);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA34_eotS =
        "\21\uffff";
    static final String DFA34_eofS =
        "\21\uffff";
    static final String DFA34_minS =
        "\1\53\20\uffff";
    static final String DFA34_maxS =
        "\1\114\20\uffff";
    static final String DFA34_acceptS =
        "\1\uffff\1\2\17\1";
    static final String DFA34_specialS =
        "\21\uffff}>";
    static final String[] DFA34_transitionS = {
            "\1\17\1\3\1\20\1\16\1\1\2\uffff\1\12\1\6\2\uffff\1\10\1\11"+
            "\4\6\1\4\1\13\1\6\1\7\1\uffff\1\14\1\15\5\uffff\1\7\1\2\2\uffff"+
            "\1\5",
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

    class DFA34 extends DFA {

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
        public String getDescription() {
            return "()+ loopback of 607:9: ( ( or )? ftsFieldGroupExplicitConjunction )+";
        }
    }
    static final String DFA33_eotS =
        "\32\uffff";
    static final String DFA33_eofS =
        "\32\uffff";
    static final String DFA33_minS =
        "\1\53\1\uffff\1\54\27\uffff";
    static final String DFA33_maxS =
        "\1\114\1\uffff\1\110\27\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\1\1\uffff\15\2\1\1\11\2";
    static final String DFA33_specialS =
        "\32\uffff}>";
    static final String[] DFA33_transitionS = {
            "\1\16\1\2\1\17\1\15\3\uffff\1\11\1\5\2\uffff\1\7\1\10\4\5\1"+
            "\3\1\12\1\5\1\6\1\uffff\1\13\1\14\5\uffff\1\6\1\1\2\uffff\1"+
            "\4",
            "",
            "\1\20\1\uffff\1\31\3\uffff\1\25\1\21\2\uffff\1\23\1\24\4\21"+
            "\2\26\1\21\1\22\1\uffff\1\27\1\30\5\uffff\1\22",
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

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "607:10: ( or )?";
        }
    }
    static final String DFA37_eotS =
        "\35\uffff";
    static final String DFA37_eofS =
        "\35\uffff";
    static final String DFA37_minS =
        "\1\53\1\uffff\1\54\32\uffff";
    static final String DFA37_maxS =
        "\1\114\1\uffff\1\110\32\uffff";
    static final String DFA37_acceptS =
        "\1\uffff\1\2\1\uffff\1\2\17\1\1\2\11\1";
    static final String DFA37_specialS =
        "\35\uffff}>";
    static final String[] DFA37_transitionS = {
            "\1\21\1\2\1\22\1\20\1\3\2\uffff\1\14\1\10\2\uffff\1\12\1\13"+
            "\4\10\1\6\1\15\1\10\1\11\1\uffff\1\16\1\17\5\uffff\1\11\1\1"+
            "\1\4\1\5\1\7",
            "",
            "\1\23\1\uffff\1\34\3\uffff\1\30\1\24\2\uffff\1\26\1\27\4\24"+
            "\2\31\1\24\1\25\1\uffff\1\32\1\33\5\uffff\1\25",
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
            ""
    };

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "()+ loopback of 625:9: ( ( and )? ftsFieldGroupPrefixed )+";
        }
    }
    static final String DFA36_eotS =
        "\21\uffff";
    static final String DFA36_eofS =
        "\21\uffff";
    static final String DFA36_minS =
        "\1\53\20\uffff";
    static final String DFA36_maxS =
        "\1\114\20\uffff";
    static final String DFA36_acceptS =
        "\1\uffff\2\1\16\2";
    static final String DFA36_specialS =
        "\21\uffff}>";
    static final String[] DFA36_transitionS = {
            "\1\16\1\17\1\20\1\15\3\uffff\1\11\1\5\2\uffff\1\7\1\10\4\5"+
            "\1\3\1\12\1\5\1\6\1\uffff\1\13\1\14\5\uffff\1\6\1\uffff\1\1"+
            "\1\2\1\4",
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

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "625:10: ( and )?";
        }
    }
    static final String DFA43_eotS =
        "\17\uffff";
    static final String DFA43_eofS =
        "\17\uffff";
    static final String DFA43_minS =
        "\1\53\1\0\15\uffff";
    static final String DFA43_maxS =
        "\1\114\1\0\15\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\1\11\2\1\3\1\4\1\5";
    static final String DFA43_specialS =
        "\1\0\1\1\15\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\14\1\15\1\16\1\13\3\uffff\1\7\1\3\2\uffff\1\5\1\6\4\3\1"+
            "\1\1\10\1\3\1\4\1\uffff\1\11\1\12\5\uffff\1\4\3\uffff\1\2",
            "\1\uffff",
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

    static final short[] DFA43_eot = DFA.unpackEncodedString(DFA43_eotS);
    static final short[] DFA43_eof = DFA.unpackEncodedString(DFA43_eofS);
    static final char[] DFA43_min = DFA.unpackEncodedStringToUnsignedChars(DFA43_minS);
    static final char[] DFA43_max = DFA.unpackEncodedStringToUnsignedChars(DFA43_maxS);
    static final short[] DFA43_accept = DFA.unpackEncodedString(DFA43_acceptS);
    static final short[] DFA43_special = DFA.unpackEncodedString(DFA43_specialS);
    static final short[][] DFA43_transition;

    static {
        int numStates = DFA43_transitionS.length;
        DFA43_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA43_transition[i] = DFA.unpackEncodedString(DFA43_transitionS[i]);
        }
    }

    class DFA43 extends DFA {

        public DFA43(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 43;
            this.eot = DFA43_eot;
            this.eof = DFA43_eof;
            this.min = DFA43_min;
            this.max = DFA43_max;
            this.accept = DFA43_accept;
            this.special = DFA43_special;
            this.transition = DFA43_transition;
        }
        public String getDescription() {
            return "630:1: ftsFieldGroupPrefixed : ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA43_0 = input.LA(1);

                         
                        int index43_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA43_0==NOT) ) {s = 1;}

                        else if ( (LA43_0==EXCLAMATION) && (synpred9_FTS())) {s = 2;}

                        else if ( (LA43_0==DECIMAL_INTEGER_LITERAL||(LA43_0>=ID && LA43_0<=FTSWILD)||LA43_0==FLOATING_POINT_LITERAL) ) {s = 3;}

                        else if ( (LA43_0==STAR||LA43_0==QUESTION_MARK) ) {s = 4;}

                        else if ( (LA43_0==EQUALS) ) {s = 5;}

                        else if ( (LA43_0==FTSPHRASE) ) {s = 6;}

                        else if ( (LA43_0==TILDA) ) {s = 7;}

                        else if ( (LA43_0==TO) ) {s = 8;}

                        else if ( (LA43_0==LSQUARE) ) {s = 9;}

                        else if ( (LA43_0==LT) ) {s = 10;}

                        else if ( (LA43_0==LPAREN) ) {s = 11;}

                        else if ( (LA43_0==PLUS) ) {s = 12;}

                        else if ( (LA43_0==BAR) ) {s = 13;}

                        else if ( (LA43_0==MINUS) ) {s = 14;}

                         
                        input.seek(index43_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA43_1 = input.LA(1);

                         
                        int index43_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_FTS()) ) {s = 2;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index43_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 43, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA50_eotS =
        "\133\uffff";
    static final String DFA50_eofS =
        "\133\uffff";
    static final String DFA50_minS =
        "\1\56\1\53\1\uffff\1\63\1\53\1\63\1\53\3\uffff\1\53\53\uffff\1"+
        "\53\1\0\4\uffff\2\0\1\53\22\uffff\1\53\11\uffff";
    static final String DFA50_maxS =
        "\1\110\1\114\1\uffff\1\110\1\114\1\110\1\114\3\uffff\1\114\53\uffff"+
        "\1\114\1\0\4\uffff\2\0\1\114\22\uffff\1\114\11\uffff";
    static final String DFA50_acceptS =
        "\2\uffff\1\2\4\uffff\2\10\1\11\1\uffff\23\2\1\10\1\3\1\5\23\4\1"+
        "\6\1\7\2\uffff\4\2\3\uffff\1\1\21\2\1\uffff\11\2";
    static final String DFA50_specialS =
        "\66\uffff\1\0\1\2\4\uffff\1\3\1\1\35\uffff}>";
    static final String[] DFA50_transitionS = {
            "\1\11\3\uffff\1\5\1\1\2\uffff\1\3\1\4\4\1\2\6\1\1\1\2\1\uffff"+
            "\1\7\1\10\5\uffff\1\2",
            "\1\34\1\21\1\35\1\33\1\17\2\uffff\1\13\1\24\1\14\1\uffff\1"+
            "\26\1\27\4\24\1\22\1\30\1\24\1\12\1\36\1\31\1\32\5\uffff\1\25"+
            "\1\20\1\15\1\16\1\23",
            "",
            "\1\37\2\uffff\1\40\1\uffff\10\37\10\uffff\1\37",
            "\1\62\1\47\1\63\1\61\1\45\2\uffff\1\41\1\52\1\42\1\uffff\1"+
            "\54\1\55\4\52\1\50\1\56\1\52\1\53\1\36\1\57\1\60\5\uffff\1\53"+
            "\1\46\1\43\1\44\1\51",
            "\1\65\2\uffff\1\64\1\uffff\10\65\10\uffff\1\65",
            "\1\34\1\21\1\35\1\33\1\17\2\uffff\1\13\1\24\1\14\1\uffff\1"+
            "\26\1\27\4\24\1\22\1\30\1\24\1\12\1\uffff\1\31\1\32\5\uffff"+
            "\1\25\1\20\1\15\1\16\1\23",
            "",
            "",
            "",
            "\1\34\1\21\1\35\1\66\1\17\2\uffff\1\70\1\74\1\71\1\uffff\1"+
            "\26\1\27\4\74\1\67\1\75\1\74\1\25\1\uffff\1\31\1\32\5\uffff"+
            "\1\25\1\20\1\72\1\73\1\23",
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
            "\1\115\1\116\1\117\1\114\1\77\2\uffff\1\110\1\76\2\uffff\1"+
            "\106\1\107\4\104\1\102\1\111\1\104\1\105\1\uffff\1\112\1\113"+
            "\5\uffff\1\105\1\120\1\100\1\101\1\103",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\115\1\126\1\117\1\114\1\121\2\uffff\1\123\1\104\1\124\1"+
            "\uffff\1\106\1\107\4\104\1\102\1\111\1\104\1\122\1\131\1\112"+
            "\1\113\5\uffff\1\105\1\125\1\127\1\130\1\103",
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
            "\1\34\1\21\1\35\1\33\1\17\2\uffff\1\132\1\74\1\71\1\uffff"+
            "\1\26\1\27\4\74\1\67\1\75\1\74\1\25\1\uffff\1\31\1\32\5\uffff"+
            "\1\25\1\20\1\72\1\73\1\23",
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

    static final short[] DFA50_eot = DFA.unpackEncodedString(DFA50_eotS);
    static final short[] DFA50_eof = DFA.unpackEncodedString(DFA50_eofS);
    static final char[] DFA50_min = DFA.unpackEncodedStringToUnsignedChars(DFA50_minS);
    static final char[] DFA50_max = DFA.unpackEncodedStringToUnsignedChars(DFA50_maxS);
    static final short[] DFA50_accept = DFA.unpackEncodedString(DFA50_acceptS);
    static final short[] DFA50_special = DFA.unpackEncodedString(DFA50_specialS);
    static final short[][] DFA50_transition;

    static {
        int numStates = DFA50_transitionS.length;
        DFA50_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA50_transition[i] = DFA.unpackEncodedString(DFA50_transitionS[i]);
        }
    }

    class DFA50 extends DFA {

        public DFA50(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 50;
            this.eot = DFA50_eot;
            this.eof = DFA50_eof;
            this.min = DFA50_min;
            this.max = DFA50_max;
            this.accept = DFA50_accept;
            this.special = DFA50_special;
            this.transition = DFA50_transition;
        }
        public String getDescription() {
            return "649:1: ftsFieldGroupTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA50_54 = input.LA(1);

                         
                        int index50_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA50_54==DECIMAL_INTEGER_LITERAL) ) {s = 62;}

                        else if ( (LA50_54==RPAREN) && (synpred10_FTS())) {s = 63;}

                        else if ( (LA50_54==AND) ) {s = 64;}

                        else if ( (LA50_54==AMP) ) {s = 65;}

                        else if ( (LA50_54==NOT) ) {s = 66;}

                        else if ( (LA50_54==EXCLAMATION) ) {s = 67;}

                        else if ( ((LA50_54>=ID && LA50_54<=FTSWILD)||LA50_54==FLOATING_POINT_LITERAL) ) {s = 68;}

                        else if ( (LA50_54==STAR||LA50_54==QUESTION_MARK) ) {s = 69;}

                        else if ( (LA50_54==EQUALS) ) {s = 70;}

                        else if ( (LA50_54==FTSPHRASE) ) {s = 71;}

                        else if ( (LA50_54==TILDA) ) {s = 72;}

                        else if ( (LA50_54==TO) ) {s = 73;}

                        else if ( (LA50_54==LSQUARE) ) {s = 74;}

                        else if ( (LA50_54==LT) ) {s = 75;}

                        else if ( (LA50_54==LPAREN) ) {s = 76;}

                        else if ( (LA50_54==PLUS) ) {s = 77;}

                        else if ( (LA50_54==BAR) ) {s = 78;}

                        else if ( (LA50_54==MINUS) ) {s = 79;}

                        else if ( (LA50_54==OR) ) {s = 80;}

                         
                        input.seek(index50_54);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA50_61 = input.LA(1);

                         
                        int index50_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_FTS()) ) {s = 63;}

                        else if ( (true) ) {s = 80;}

                         
                        input.seek(index50_61);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA50_55 = input.LA(1);

                         
                        int index50_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_FTS()) ) {s = 63;}

                        else if ( (true) ) {s = 80;}

                         
                        input.seek(index50_55);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA50_60 = input.LA(1);

                         
                        int index50_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_FTS()) ) {s = 63;}

                        else if ( (true) ) {s = 80;}

                         
                        input.seek(index50_60);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 50, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA44_eotS =
        "\30\uffff";
    static final String DFA44_eofS =
        "\30\uffff";
    static final String DFA44_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA44_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA44_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA44_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\6\26\1\24\1\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA44_eot = DFA.unpackEncodedString(DFA44_eotS);
    static final short[] DFA44_eof = DFA.unpackEncodedString(DFA44_eofS);
    static final char[] DFA44_min = DFA.unpackEncodedStringToUnsignedChars(DFA44_minS);
    static final char[] DFA44_max = DFA.unpackEncodedStringToUnsignedChars(DFA44_maxS);
    static final short[] DFA44_accept = DFA.unpackEncodedString(DFA44_acceptS);
    static final short[] DFA44_special = DFA.unpackEncodedString(DFA44_specialS);
    static final short[][] DFA44_transition;

    static {
        int numStates = DFA44_transitionS.length;
        DFA44_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA44_transition[i] = DFA.unpackEncodedString(DFA44_transitionS[i]);
        }
    }

    class DFA44 extends DFA {

        public DFA44(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 44;
            this.eot = DFA44_eot;
            this.eof = DFA44_eof;
            this.min = DFA44_min;
            this.max = DFA44_max;
            this.accept = DFA44_accept;
            this.special = DFA44_special;
            this.transition = DFA44_transition;
        }
        public String getDescription() {
            return "654:29: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA44_20 = input.LA(1);

                         
                        int index44_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index44_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 44, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA45_eotS =
        "\30\uffff";
    static final String DFA45_eofS =
        "\30\uffff";
    static final String DFA45_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA45_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA45_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA45_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA45_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\6\26\1\24\1\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        public String getDescription() {
            return "657:34: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA45_20 = input.LA(1);

                         
                        int index45_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index45_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 45, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA46_eotS =
        "\30\uffff";
    static final String DFA46_eofS =
        "\30\uffff";
    static final String DFA46_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA46_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA46_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA46_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA46_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\10\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        public String getDescription() {
            return "660:31: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA46_20 = input.LA(1);

                         
                        int index46_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index46_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 46, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA47_eotS =
        "\30\uffff";
    static final String DFA47_eofS =
        "\30\uffff";
    static final String DFA47_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA47_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA47_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA47_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\10\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        public String getDescription() {
            return "663:36: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_20 = input.LA(1);

                         
                        int index47_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index47_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA48_eotS =
        "\30\uffff";
    static final String DFA48_eofS =
        "\30\uffff";
    static final String DFA48_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA48_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA48_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA48_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA48_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\10\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        public String getDescription() {
            return "666:40: ( ( slop )=> slop )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA48_20 = input.LA(1);

                         
                        int index48_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index48_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 48, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA49_eotS =
        "\30\uffff";
    static final String DFA49_eofS =
        "\30\uffff";
    static final String DFA49_minS =
        "\1\53\1\63\22\uffff\1\0\3\uffff";
    static final String DFA49_maxS =
        "\1\114\1\110\22\uffff\1\0\3\uffff";
    static final String DFA49_acceptS =
        "\2\uffff\22\2\1\uffff\2\2\1\1";
    static final String DFA49_specialS =
        "\24\uffff\1\0\3\uffff}>";
    static final String[] DFA49_transitionS = {
            "\1\22\1\7\1\23\1\21\1\5\2\uffff\1\1\1\12\1\2\1\uffff\1\14\1"+
            "\15\4\12\1\10\1\16\1\12\1\13\1\uffff\1\17\1\20\5\uffff\1\13"+
            "\1\6\1\3\1\4\1\11",
            "\1\24\2\uffff\1\25\1\uffff\6\26\1\24\1\26\10\uffff\1\26",
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
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "669:32: ( ( fuzzy )=> fuzzy )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA49_20 = input.LA(1);

                         
                        int index49_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred16_FTS()) ) {s = 23;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index49_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 49, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA51_eotS =
        "\105\uffff";
    static final String DFA51_eofS =
        "\1\4\15\uffff\1\36\57\uffff\1\36\6\uffff";
    static final String DFA51_minS =
        "\1\53\15\uffff\1\53\20\uffff\4\0\1\53\12\uffff\1\53\17\uffff\1"+
        "\53\6\uffff";
    static final String DFA51_maxS =
        "\1\114\15\uffff\1\114\20\uffff\4\0\1\114\12\uffff\1\114\17\uffff"+
        "\1\114\6\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\15\2\1\uffff\20\2\5\uffff\1\1\11\2\1\uffff\14\2\1\1\2"+
        "\2\1\uffff\6\2";
    static final String DFA51_specialS =
        "\37\uffff\1\2\1\0\1\1\1\4\1\3\41\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\27\1\7\1\30\1\25\1\5\1\26\1\uffff\1\21\1\14\1\1\1\uffff"+
            "\1\17\1\20\1\12\3\14\1\10\1\22\1\14\1\16\1\uffff\1\23\1\24\2"+
            "\uffff\1\13\1\15\1\uffff\1\31\1\6\1\2\1\3\1\11",
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
            "\1\27\1\7\1\30\1\43\1\5\1\26\1\uffff\1\32\1\41\1\33\1\uffff"+
            "\1\17\1\20\1\40\3\41\1\37\1\42\1\41\1\31\1\uffff\1\23\1\24\2"+
            "\uffff\1\13\1\15\1\uffff\1\31\1\6\1\34\1\35\1\11",
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
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\67\1\70\1\47\1\65\1\73\1\66\1\uffff\1\61\1\56\2\uffff\1"+
            "\60\1\46\1\52\3\72\1\45\1\62\1\72\1\54\1\uffff\1\63\1\64\2\uffff"+
            "\1\55\1\57\1\uffff\1\54\1\71\1\50\1\51\1\53",
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
            "\1\67\1\75\1\47\1\65\1\76\1\66\1\uffff\1\100\1\72\1\101\1"+
            "\uffff\1\60\1\46\1\52\3\72\1\45\1\62\1\72\1\77\1\104\1\63\1"+
            "\64\2\uffff\1\55\1\57\1\uffff\1\54\1\74\1\102\1\103\1\53",
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
            "\1\27\1\7\1\30\1\25\1\5\1\26\1\uffff\1\21\1\41\1\33\1\uffff"+
            "\1\17\1\20\1\40\3\41\1\37\1\42\1\41\1\31\1\uffff\1\23\1\24\2"+
            "\uffff\1\13\1\15\1\uffff\1\31\1\6\1\34\1\35\1\11",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "()+ loopback of 715:36: ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_32 = input.LA(1);

                         
                        int index51_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred17_FTS()) ) {s = 36;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index51_32);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA51_33 = input.LA(1);

                         
                        int index51_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred17_FTS()) ) {s = 36;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index51_33);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA51_31 = input.LA(1);

                         
                        int index51_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred17_FTS()) ) {s = 36;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index51_31);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA51_35 = input.LA(1);

                         
                        int index51_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA51_35==NOT) ) {s = 37;}

                        else if ( (LA51_35==FTSPHRASE) ) {s = 38;}

                        else if ( (LA51_35==MINUS) ) {s = 39;}

                        else if ( (LA51_35==AND) ) {s = 40;}

                        else if ( (LA51_35==AMP) ) {s = 41;}

                        else if ( (LA51_35==ID) ) {s = 42;}

                        else if ( (LA51_35==EXCLAMATION) ) {s = 43;}

                        else if ( (LA51_35==STAR||LA51_35==QUESTION_MARK) ) {s = 44;}

                        else if ( (LA51_35==AT) ) {s = 45;}

                        else if ( (LA51_35==DECIMAL_INTEGER_LITERAL) ) {s = 46;}

                        else if ( (LA51_35==URI) ) {s = 47;}

                        else if ( (LA51_35==EQUALS) ) {s = 48;}

                        else if ( (LA51_35==TILDA) ) {s = 49;}

                        else if ( (LA51_35==TO) ) {s = 50;}

                        else if ( (LA51_35==LSQUARE) ) {s = 51;}

                        else if ( (LA51_35==LT) ) {s = 52;}

                        else if ( (LA51_35==LPAREN) ) {s = 53;}

                        else if ( (LA51_35==PERCENT) ) {s = 54;}

                        else if ( (LA51_35==PLUS) ) {s = 55;}

                        else if ( (LA51_35==BAR) ) {s = 56;}

                        else if ( (LA51_35==OR) ) {s = 57;}

                        else if ( ((LA51_35>=FTSWORD && LA51_35<=FTSWILD)||LA51_35==FLOATING_POINT_LITERAL) ) {s = 58;}

                        else if ( (LA51_35==RPAREN) && (synpred17_FTS())) {s = 59;}

                         
                        input.seek(index51_35);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA51_34 = input.LA(1);

                         
                        int index51_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred17_FTS()) ) {s = 36;}

                        else if ( (true) ) {s = 30;}

                         
                        input.seek(index51_34);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA58_eotS =
        "\50\uffff";
    static final String DFA58_eofS =
        "\7\uffff\1\23\40\uffff";
    static final String DFA58_minS =
        "\1\70\1\65\1\uffff\1\70\1\56\1\65\1\uffff\1\53\40\uffff";
    static final String DFA58_maxS =
        "\1\106\1\107\1\uffff\1\70\1\110\1\65\1\uffff\1\114\40\uffff";
    static final String DFA58_acceptS =
        "\2\uffff\1\2\3\uffff\1\3\1\uffff\5\3\2\1\31\3";
    static final String DFA58_specialS =
        "\50\uffff}>";
    static final String[] DFA58_transitionS = {
            "\1\1\15\uffff\1\2",
            "\1\4\21\uffff\1\3",
            "",
            "\1\5",
            "\1\6\4\uffff\1\10\3\uffff\1\11\1\7\3\10\2\12\1\10\1\12\1\uffff"+
            "\1\13\1\14\5\uffff\1\12",
            "\1\4",
            "",
            "\1\45\1\26\1\46\1\43\1\24\1\44\1\uffff\1\17\1\33\1\20\1\16"+
            "\1\36\1\37\1\31\3\33\1\27\1\40\1\33\1\35\1\47\1\41\1\42\2\uffff"+
            "\1\32\1\34\1\15\1\35\1\25\1\21\1\22\1\30",
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
            ""
    };

    static final short[] DFA58_eot = DFA.unpackEncodedString(DFA58_eotS);
    static final short[] DFA58_eof = DFA.unpackEncodedString(DFA58_eofS);
    static final char[] DFA58_min = DFA.unpackEncodedStringToUnsignedChars(DFA58_minS);
    static final char[] DFA58_max = DFA.unpackEncodedStringToUnsignedChars(DFA58_maxS);
    static final short[] DFA58_accept = DFA.unpackEncodedString(DFA58_acceptS);
    static final short[] DFA58_special = DFA.unpackEncodedString(DFA58_specialS);
    static final short[][] DFA58_transition;

    static {
        int numStates = DFA58_transitionS.length;
        DFA58_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA58_transition[i] = DFA.unpackEncodedString(DFA58_transitionS[i]);
        }
    }

    class DFA58 extends DFA {

        public DFA58(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 58;
            this.eot = DFA58_eot;
            this.eof = DFA58_eof;
            this.min = DFA58_min;
            this.max = DFA58_max;
            this.accept = DFA58_accept;
            this.special = DFA58_special;
            this.transition = DFA58_transition;
        }
        public String getDescription() {
            return "767:9: ( prefix | uri )?";
        }
    }
    static final String DFA60_eotS =
        "\37\uffff";
    static final String DFA60_eofS =
        "\1\uffff\1\10\34\uffff\1\10";
    static final String DFA60_minS =
        "\1\70\1\53\1\uffff\1\70\32\uffff\1\53";
    static final String DFA60_maxS =
        "\1\106\1\114\1\uffff\1\70\32\uffff\1\114";
    static final String DFA60_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\31\3\1\uffff";
    static final String DFA60_specialS =
        "\37\uffff}>";
    static final String[] DFA60_transitionS = {
            "\1\1\15\uffff\1\2",
            "\1\33\1\13\1\34\1\31\1\11\1\32\1\35\1\25\1\20\1\5\1\4\1\23"+
            "\1\24\1\16\3\20\1\14\1\26\1\20\1\22\1\uffff\1\27\1\30\2\uffff"+
            "\1\17\1\21\1\3\1\22\1\12\1\6\1\7\1\15",
            "",
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
            "\1\33\1\13\1\34\1\31\1\11\1\32\1\35\1\25\1\20\1\5\1\4\1\23"+
            "\1\24\1\16\3\20\1\14\1\26\1\20\1\22\1\uffff\1\27\1\30\2\uffff"+
            "\1\17\1\21\1\uffff\1\22\1\12\1\6\1\7\1\15"
    };

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "779:9: ( prefix | uri )?";
        }
    }
    static final String DFA61_eotS =
        "\35\uffff";
    static final String DFA61_eofS =
        "\1\uffff\1\7\33\uffff";
    static final String DFA61_minS =
        "\1\70\1\53\33\uffff";
    static final String DFA61_maxS =
        "\1\70\1\114\33\uffff";
    static final String DFA61_acceptS =
        "\2\uffff\1\2\32\1";
    static final String DFA61_specialS =
        "\35\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\1",
            "\1\32\1\12\1\33\1\30\1\10\1\31\1\34\1\24\1\17\1\4\1\3\1\22"+
            "\1\23\1\15\3\17\1\13\1\25\1\17\1\21\1\uffff\1\26\1\27\2\uffff"+
            "\1\16\1\20\1\2\1\21\1\11\1\5\1\6\1\14",
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
            ""
    };

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "802:1: identifier : ( ID -> ID | id1= ID DOT id2= ID ->);";
        }
    }
 

    public static final BitSet FOLLOW_ftsDisjunction_in_ftsQuery559 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ftsQuery561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cmisExplicitDisjunction_in_ftsDisjunction620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsExplicitDisjunction_in_ftsDisjunction635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsImplicitDisjunction_in_ftsDisjunction650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction683 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_or_in_ftsExplicitDisjunction686 = new BitSet(new long[]{0xFFCD780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction688 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction772 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_or_in_cmisExplicitDisjunction775 = new BitSet(new long[]{0xFF88200000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction777 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_or_in_ftsImplicitDisjunction862 = new BitSet(new long[]{0xFFCD780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsExplicitConjunction_in_ftsImplicitDisjunction865 = new BitSet(new long[]{0xFFCD780000000002L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction952 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_and_in_ftsExplicitConjunction955 = new BitSet(new long[]{0xFFCD780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction957 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_and_in_ftsImplicitConjunction1042 = new BitSet(new long[]{0xFFCD780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsPrefixed_in_ftsImplicitConjunction1045 = new BitSet(new long[]{0xFFCD780000000002L,0x0000000000001D66L});
    public static final BitSet FOLLOW_cmisPrefixed_in_cmisConjunction1129 = new BitSet(new long[]{0xFF88200000000002L,0x0000000000000160L});
    public static final BitSet FOLLOW_not_in_ftsPrefixed1221 = new BitSet(new long[]{0xFFCD400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1223 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsPrefixed1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1289 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsPrefixed1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ftsPrefixed1355 = new BitSet(new long[]{0xFFCD400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1357 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsPrefixed1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_ftsPrefixed1423 = new BitSet(new long[]{0xFFCD400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1425 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsPrefixed1427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_ftsPrefixed1491 = new BitSet(new long[]{0xFFCD400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1493 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsPrefixed1495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_cmisPrefixed1640 = new BitSet(new long[]{0xFF88000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsTest1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsTerm_in_ftsTest1792 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsTest1802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsExactTerm_in_ftsTest1867 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsTest1877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsPhrase_in_ftsTest1942 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsTest1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsExactPhrase_in_ftsTest2017 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsTest2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsTokenisedPhrase_in_ftsTest2092 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsTest2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsSynonym_in_ftsTest2167 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsTest2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsRange_in_ftsTest2242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroup_in_ftsTest2302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_ftsTest2334 = new BitSet(new long[]{0xFFCD780000000000L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsDisjunction_in_ftsTest2336 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RPAREN_in_ftsTest2338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_ftsTest2370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cmisTerm_in_cmisTest2423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cmisPhrase_in_cmisTest2483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENT_in_template2564 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_tempReference_in_template2566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENT_in_template2626 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_LPAREN_in_template2628 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_tempReference_in_template2631 = new BitSet(new long[]{0x0102800000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_COMMA_in_template2633 = new BitSet(new long[]{0x0100800000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_RPAREN_in_template2638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_fuzzy2720 = new BitSet(new long[]{0x4008000000000000L});
    public static final BitSet FOLLOW_number_in_fuzzy2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_slop2803 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARAT_in_boost2886 = new BitSet(new long[]{0x4008000000000000L});
    public static final BitSet FOLLOW_number_in_boost2888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldReference_in_ftsTerm2970 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_COLON_in_ftsTerm2972 = new BitSet(new long[]{0xFF08000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_ftsWord_in_ftsTerm2976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsWord_in_cmisTerm3032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ftsExactTerm3085 = new BitSet(new long[]{0xFF08000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_ftsTerm_in_ftsExactTerm3087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldReference_in_ftsPhrase3141 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_COLON_in_ftsPhrase3143 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_FTSPHRASE_in_ftsPhrase3147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ftsExactPhrase3211 = new BitSet(new long[]{0x0180000000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_ftsPhrase_in_ftsExactPhrase3213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_ftsTokenisedPhrase3274 = new BitSet(new long[]{0x0180000000000000L,0x0000000000000060L});
    public static final BitSet FOLLOW_ftsPhrase_in_ftsTokenisedPhrase3276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FTSPHRASE_in_cmisPhrase3330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_ftsSynonym3383 = new BitSet(new long[]{0xFF08000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_ftsTerm_in_ftsSynonym3385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldReference_in_ftsRange3439 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_COLON_in_ftsRange3441 = new BitSet(new long[]{0x4F88000000000000L,0x0000000000000066L});
    public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsRange3445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldReference_in_ftsFieldGroup3501 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_COLON_in_ftsFieldGroup3503 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroup3505 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3507 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroup3509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3643 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_or_in_ftsFieldGroupExplicitDisjunction3646 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3648 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_or_in_ftsFieldGroupImplicitDisjunction3733 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction3736 = new BitSet(new long[]{0xFFCC780000000002L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction3823 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_and_in_ftsFieldGroupExplicitConjunction3826 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction3828 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_and_in_ftsFieldGroupImplicitConjunction3913 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001D66L});
    public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction3916 = new BitSet(new long[]{0xFFCC780000000002L,0x0000000000001D66L});
    public static final BitSet FOLLOW_not_in_ftsFieldGroupPrefixed4006 = new BitSet(new long[]{0xFFCC400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4008 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4074 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ftsFieldGroupPrefixed4140 = new BitSet(new long[]{0xFFCC400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4142 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_ftsFieldGroupPrefixed4208 = new BitSet(new long[]{0xFFCC400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4210 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_ftsFieldGroupPrefixed4276 = new BitSet(new long[]{0xFFCC400000000000L,0x0000000000000166L});
    public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4278 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4431 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4506 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4581 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest4656 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest4731 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest4806 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest4881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroupTest4941 = new BitSet(new long[]{0xFFCC780000000000L,0x0000000000001F66L});
    public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest4943 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroupTest4945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsWord_in_ftsFieldGroupTerm4998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5031 = new BitSet(new long[]{0xFF08000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5127 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5190 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupSynonym5245 = new BitSet(new long[]{0xFF08000000000000L,0x0000000000000160L});
    public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5300 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_proximityGroup_in_ftsFieldGroupProximity5310 = new BitSet(new long[]{0x7F08000000000000L});
    public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5312 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_set_in_ftsFieldGroupProximityTerm0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_proximityGroup5491 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_LPAREN_in_proximityGroup5494 = new BitSet(new long[]{0x0008800000000000L});
    public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5496 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RPAREN_in_proximityGroup5499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5583 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DOTDOT_in_ftsFieldGroupRange5585 = new BitSet(new long[]{0x4F88000000000000L});
    public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_left_in_ftsFieldGroupRange5625 = new BitSet(new long[]{0x4F88000000000000L});
    public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5627 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_TO_in_ftsFieldGroupRange5629 = new BitSet(new long[]{0x4F88000000000000L});
    public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5631 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000018L});
    public static final BitSet FOLLOW_range_right_in_ftsFieldGroupRange5633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_range_left5692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_range_left5724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RSQUARE_in_range_right5777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_range_right5809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_fieldReference5865 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_prefix_in_fieldReference5894 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_uri_in_fieldReference5914 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_identifier_in_fieldReference5935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_tempReference6022 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_prefix_in_tempReference6051 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_uri_in_tempReference6071 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_identifier_in_tempReference6092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_prefix6179 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_COLON_in_prefix6181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_URI_in_uri6262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier6343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier6401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_DOT_in_identifier6403 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_ID_in_identifier6407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ftsWord0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_number0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ftsRangeWord0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or6758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_or6770 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_BAR_in_or6772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and6805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMP_in_and6817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_AMP_in_and6819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_not0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_in_synpred1_FTS1216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred2_FTS1727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred3_FTS1797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred4_FTS1872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred5_FTS1947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred6_FTS2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred7_FTS2097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred8_FTS2172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_in_synpred9_FTS4001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred10_FTS4366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred11_FTS4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred12_FTS4511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred13_FTS4586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred14_FTS4661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_slop_in_synpred15_FTS4736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fuzzy_in_synpred16_FTS4811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_proximityGroup_in_synpred17_FTS5305 = new BitSet(new long[]{0x0000000000000002L});

}