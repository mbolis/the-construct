//=========================================================================
//
//  This file was generated by Mouse 1.7 at 2015-12-07 15:18:29 GMT
//  from grammar
//    '/home/developer/workspace-trunk/the-construct/parser/Commands.mouse'
//    .
//
//=========================================================================

import mouse.runtime.Source;

public class CommandParser extends mouse.runtime.ParserTest
{
  final CommandActions sem;
  
  //=======================================================================
  //
  //  Initialization
  //
  //=======================================================================
  //-------------------------------------------------------------------
  //  Constructor
  //-------------------------------------------------------------------
  public CommandParser()
    {
      sem = new CommandActions();
      sem.rule = this;
      super.sem = sem;
      caches = cacheList;
    }
  
  //-------------------------------------------------------------------
  //  Run the parser
  //-------------------------------------------------------------------
  public boolean parse(Source src)
    {
      super.init(src);
      sem.init();
      boolean result = Period();
      closeParser(result);
      return result;
    }
  
  //-------------------------------------------------------------------
  //  Get semantics
  //-------------------------------------------------------------------
  public CommandActions semantics()
    { return sem; }
  
  //=======================================================================
  //
  //  Parsing procedures
  //
  //=======================================================================
  //=====================================================================
  //  Period = Space? Sentence (Space Then Space Sentence)* Space? EOT ;
  //=====================================================================
  private boolean Period()
    {
      if (saved(Period)) return reuse();
      Space();
      if (!Sentence()) return reject(Period);
      while (Period_0());
      Space();
      if (!EOT()) return reject(Period);
      return accept(Period);
    }
  
  //-------------------------------------------------------------------
  //  Period_0 = Space Then Space Sentence
  //-------------------------------------------------------------------
  private boolean Period_0()
    {
      if (savedInner(Period_0)) return reuseInner();
      if (!Space()) return rejectInner(Period_0);
      if (!Then()) return rejectInner(Period_0);
      if (!Space()) return rejectInner(Period_0);
      if (!Sentence()) return rejectInner(Period_0);
      return acceptInner(Period_0);
    }
  
  //=====================================================================
  //  Sentence = Say Space Speech / Tell Space Name Space Speech / Verb
  //    (Space Complement)* ;
  //=====================================================================
  private boolean Sentence()
    {
      if (saved(Sentence)) return reuse();
      if (Sentence_0()) return accept(Sentence);
      if (Sentence_1()) return accept(Sentence);
      if (Sentence_2()) return accept(Sentence);
      return reject(Sentence);
    }
  
  //-------------------------------------------------------------------
  //  Sentence_0 = Say Space Speech
  //-------------------------------------------------------------------
  private boolean Sentence_0()
    {
      if (savedInner(Sentence_0)) return reuseInner();
      if (!Say()) return rejectInner(Sentence_0);
      if (!Space()) return rejectInner(Sentence_0);
      if (!Speech()) return rejectInner(Sentence_0);
      return acceptInner(Sentence_0);
    }
  
  //-------------------------------------------------------------------
  //  Sentence_1 = Tell Space Name Space Speech
  //-------------------------------------------------------------------
  private boolean Sentence_1()
    {
      if (savedInner(Sentence_1)) return reuseInner();
      if (!Tell()) return rejectInner(Sentence_1);
      if (!Space()) return rejectInner(Sentence_1);
      if (!Name()) return rejectInner(Sentence_1);
      if (!Space()) return rejectInner(Sentence_1);
      if (!Speech()) return rejectInner(Sentence_1);
      return acceptInner(Sentence_1);
    }
  
  //-------------------------------------------------------------------
  //  Sentence_2 = Verb (Space Complement)*
  //-------------------------------------------------------------------
  private boolean Sentence_2()
    {
      if (savedInner(Sentence_2)) return reuseInner();
      if (!Verb()) return rejectInner(Sentence_2);
      while (Sentence_3());
      return acceptInner(Sentence_2);
    }
  
  //-------------------------------------------------------------------
  //  Sentence_3 = Space Complement
  //-------------------------------------------------------------------
  private boolean Sentence_3()
    {
      if (savedInner(Sentence_3)) return reuseInner();
      if (!Space()) return rejectInner(Sentence_3);
      if (!Complement()) return rejectInner(Sentence_3);
      return acceptInner(Sentence_3);
    }
  
  //=====================================================================
  //  Say = Word {&Say} ;
  //=====================================================================
  private boolean Say()
    {
      if (saved(Say)) return reuse();
      if (!Word()) return reject(Say);
      if (sem.Say()) return accept(Say);
      return reject(Say);
    }
  
  //=====================================================================
  //  Tell = Word {&Tell} ;
  //=====================================================================
  private boolean Tell()
    {
      if (saved(Tell)) return reuse();
      if (!Word()) return reject(Tell);
      if (sem.Tell()) return accept(Tell);
      return reject(Tell);
    }
  
  //=====================================================================
  //  Verb = Word {&Verb} ;
  //=====================================================================
  private boolean Verb()
    {
      if (saved(Verb)) return reuse();
      if (!Word()) return reject(Verb);
      if (sem.Verb()) return accept(Verb);
      return reject(Verb);
    }
  
  //=====================================================================
  //  Name = Word {&Name} ;
  //=====================================================================
  private boolean Name()
    {
      if (saved(Name)) return reuse();
      if (!Word()) return reject(Name);
      if (sem.Name()) return accept(Name);
      return reject(Name);
    }
  
  //=====================================================================
  //  Complement = (Preposition Space)? Nominal (Space Conjunction Space
  //    Nominal)* {Complement_0} / ":" Speech {Complement_1} ;
  //=====================================================================
  private boolean Complement()
    {
      if (saved(Complement)) return reuse();
      if (Complement_0())
      { sem.Complement_0(); return accept(Complement); }
      if (Complement_1())
      { sem.Complement_1(); return accept(Complement); }
      return reject(Complement);
    }
  
  //-------------------------------------------------------------------
  //  Complement_0 = (Preposition Space)? Nominal (Space Conjunction
  //    Space Nominal)*
  //-------------------------------------------------------------------
  private boolean Complement_0()
    {
      if (savedInner(Complement_0)) return reuseInner();
      Complement_2();
      if (!Nominal()) return rejectInner(Complement_0);
      while (Complement_3());
      return acceptInner(Complement_0);
    }
  
  //-------------------------------------------------------------------
  //  Complement_1 = ":" Speech
  //-------------------------------------------------------------------
  private boolean Complement_1()
    {
      if (savedInner(Complement_1)) return reuseInner();
      if (!next(':',$Term8)) return rejectInner(Complement_1);
      if (!Speech()) return rejectInner(Complement_1);
      return acceptInner(Complement_1);
    }
  
  //-------------------------------------------------------------------
  //  Complement_2 = Preposition Space
  //-------------------------------------------------------------------
  private boolean Complement_2()
    {
      if (savedInner(Complement_2)) return reuseInner();
      if (!Preposition()) return rejectInner(Complement_2);
      if (!Space()) return rejectInner(Complement_2);
      return acceptInner(Complement_2);
    }
  
  //-------------------------------------------------------------------
  //  Complement_3 = Space Conjunction Space Nominal
  //-------------------------------------------------------------------
  private boolean Complement_3()
    {
      if (savedInner(Complement_3)) return reuseInner();
      if (!Space()) return rejectInner(Complement_3);
      if (!Conjunction()) return rejectInner(Complement_3);
      if (!Space()) return rejectInner(Complement_3);
      if (!Nominal()) return rejectInner(Complement_3);
      return acceptInner(Complement_3);
    }
  
  //=====================================================================
  //  Preposition = Word {&Preposition} ;
  //=====================================================================
  private boolean Preposition()
    {
      if (saved(Preposition)) return reuse();
      if (!Word()) return reject(Preposition);
      if (sem.Preposition()) return accept(Preposition);
      return reject(Preposition);
    }
  
  //=====================================================================
  //  Nominal = (Article Space)? Descriptor (Space Descriptor)*
  //    {Nominal_0} / Pronoun {Nominal_1} ;
  //=====================================================================
  private boolean Nominal()
    {
      if (saved(Nominal)) return reuse();
      if (Nominal_0())
      { sem.Nominal_0(); return accept(Nominal); }
      if (Pronoun())
      { sem.Nominal_1(); return accept(Nominal); }
      return reject(Nominal);
    }
  
  //-------------------------------------------------------------------
  //  Nominal_0 = (Article Space)? Descriptor (Space Descriptor)*
  //-------------------------------------------------------------------
  private boolean Nominal_0()
    {
      if (savedInner(Nominal_0)) return reuseInner();
      Nominal_1();
      if (!Descriptor()) return rejectInner(Nominal_0);
      while (Nominal_2());
      return acceptInner(Nominal_0);
    }
  
  //-------------------------------------------------------------------
  //  Nominal_1 = Article Space
  //-------------------------------------------------------------------
  private boolean Nominal_1()
    {
      if (savedInner(Nominal_1)) return reuseInner();
      if (!Article()) return rejectInner(Nominal_1);
      if (!Space()) return rejectInner(Nominal_1);
      return acceptInner(Nominal_1);
    }
  
  //-------------------------------------------------------------------
  //  Nominal_2 = Space Descriptor
  //-------------------------------------------------------------------
  private boolean Nominal_2()
    {
      if (savedInner(Nominal_2)) return reuseInner();
      if (!Space()) return rejectInner(Nominal_2);
      if (!Descriptor()) return rejectInner(Nominal_2);
      return acceptInner(Nominal_2);
    }
  
  //=====================================================================
  //  Descriptor = Word Genitive? {Descriptor_0} / Preposition Space
  //    Descriptor {Descriptor_1} ;
  //=====================================================================
  private boolean Descriptor()
    {
      if (saved(Descriptor)) return reuse();
      if (Descriptor_0())
      { sem.Descriptor_0(); return accept(Descriptor); }
      if (Descriptor_1())
      { sem.Descriptor_1(); return accept(Descriptor); }
      return reject(Descriptor);
    }
  
  //-------------------------------------------------------------------
  //  Descriptor_0 = Word Genitive?
  //-------------------------------------------------------------------
  private boolean Descriptor_0()
    {
      if (savedInner(Descriptor_0)) return reuseInner();
      if (!Word()) return rejectInner(Descriptor_0);
      Genitive();
      return acceptInner(Descriptor_0);
    }
  
  //-------------------------------------------------------------------
  //  Descriptor_1 = Preposition Space Descriptor
  //-------------------------------------------------------------------
  private boolean Descriptor_1()
    {
      if (savedInner(Descriptor_1)) return reuseInner();
      if (!Preposition()) return rejectInner(Descriptor_1);
      if (!Space()) return rejectInner(Descriptor_1);
      if (!Descriptor()) return rejectInner(Descriptor_1);
      return acceptInner(Descriptor_1);
    }
  
  //=====================================================================
  //  Genitive = "'s" ;
  //=====================================================================
  private boolean Genitive()
    {
      if (saved(Genitive)) return reuse();
      if (!next("'s",$Term17)) return reject(Genitive);
      return accept(Genitive);
    }
  
  //=====================================================================
  //  Speech = _++ EOT / """ _++ (""" / EOT) / "'" _++ ("'" / EOT) ;
  //=====================================================================
  private boolean Speech()
    {
      if (saved(Speech)) return reuse();
      if (Speech_0()) return accept(Speech);
      if (Speech_1()) return accept(Speech);
      if (Speech_2()) return accept(Speech);
      return reject(Speech);
    }
  
  //-------------------------------------------------------------------
  //  Speech_0 = _++ EOT
  //-------------------------------------------------------------------
  private boolean Speech_0()
    {
      if (savedInner(Speech_0)) return reuseInner();
      if (EOT()) return rejectInner(Speech_0);
      do if (!next($Term14)) return rejectInner(Speech_0);
        while (!EOT());
      return acceptInner(Speech_0);
    }
  
  //-------------------------------------------------------------------
  //  Speech_1 = """ _++ (""" / EOT)
  //-------------------------------------------------------------------
  private boolean Speech_1()
    {
      if (savedInner(Speech_1)) return reuseInner();
      if (!next('"',$Term1)) return rejectInner(Speech_1);
      if (Speech_3()) return rejectInner(Speech_1);
      do if (!next($Term11)) return rejectInner(Speech_1);
        while (!Speech_3());
      return acceptInner(Speech_1);
    }
  
  //-------------------------------------------------------------------
  //  Speech_2 = "'" _++ ("'" / EOT)
  //-------------------------------------------------------------------
  private boolean Speech_2()
    {
      if (savedInner(Speech_2)) return reuseInner();
      if (!next('\'',$Term3)) return rejectInner(Speech_2);
      if (Speech_4()) return rejectInner(Speech_2);
      do if (!next($Term10)) return rejectInner(Speech_2);
        while (!Speech_4());
      return acceptInner(Speech_2);
    }
  
  //-------------------------------------------------------------------
  //  Speech_3 = """ / EOT
  //-------------------------------------------------------------------
  private boolean Speech_3()
    {
      if (savedInner(Speech_3)) return reuseInner();
      if (next('"',$Term1)) return acceptInner(Speech_3);
      if (EOT()) return acceptInner(Speech_3);
      return rejectInner(Speech_3);
    }
  
  //-------------------------------------------------------------------
  //  Speech_4 = "'" / EOT
  //-------------------------------------------------------------------
  private boolean Speech_4()
    {
      if (savedInner(Speech_4)) return reuseInner();
      if (next('\'',$Term3)) return acceptInner(Speech_4);
      if (EOT()) return acceptInner(Speech_4);
      return rejectInner(Speech_4);
    }
  
  //=====================================================================
  //  Word = ^[ \t\n\r]+ ;
  //=====================================================================
  private boolean Word()
    {
      if (saved(Word)) return reuse();
      if (!nextNotIn(" \t\n\r",$Term15)) return reject(Word);
      while (nextNotIn(" \t\n\r",$Term15));
      return accept(Word);
    }
  
  //=====================================================================
  //  Space = [ \t]+ ;
  //=====================================================================
  private boolean Space()
    {
      if (saved(Space)) return reuse();
      if (!nextIn(" \t",$Term16)) return reject(Space);
      while (nextIn(" \t",$Term16));
      return accept(Space);
    }
  
  //=====================================================================
  //  Then = [tT] [hH] [eE] [nN] ;
  //=====================================================================
  private boolean Then()
    {
      if (saved(Then)) return reuse();
      if (!nextIn("tT",$Term12)) return reject(Then);
      if (!nextIn("hH",$Term4)) return reject(Then);
      if (!nextIn("eE",$Term6)) return reject(Then);
      if (!nextIn("nN",$Term7)) return reject(Then);
      return accept(Then);
    }
  
  //=====================================================================
  //  Conjunction = And / Comma ;
  //=====================================================================
  private boolean Conjunction()
    {
      if (saved(Conjunction)) return reuse();
      if (And()) return accept(Conjunction);
      if (Comma()) return accept(Conjunction);
      return reject(Conjunction);
    }
  
  //=====================================================================
  //  And = [aA] [nN] [dD] ;
  //=====================================================================
  private boolean And()
    {
      if (saved(And)) return reuse();
      if (!nextIn("aA",$Term0)) return reject(And);
      if (!nextIn("nN",$Term7)) return reject(And);
      if (!nextIn("dD",$Term5)) return reject(And);
      return accept(And);
    }
  
  //=====================================================================
  //  Comma = "," ;
  //=====================================================================
  private boolean Comma()
    {
      if (saved(Comma)) return reuse();
      if (!next(',',$Term13)) return reject(Comma);
      return accept(Comma);
    }
  
  //=====================================================================
  //  Article = Definite / Indefinite ;
  //=====================================================================
  private boolean Article()
    {
      if (saved(Article)) return reuse();
      if (Definite()) return accept(Article);
      if (Indefinite()) return accept(Article);
      return reject(Article);
    }
  
  //=====================================================================
  //  Definite = [tT] [hH] [eE] ;
  //=====================================================================
  private boolean Definite()
    {
      if (saved(Definite)) return reuse();
      if (!nextIn("tT",$Term12)) return reject(Definite);
      if (!nextIn("hH",$Term4)) return reject(Definite);
      if (!nextIn("eE",$Term6)) return reject(Definite);
      return accept(Definite);
    }
  
  //=====================================================================
  //  Indefinite = [aA] [nN]? ;
  //=====================================================================
  private boolean Indefinite()
    {
      if (saved(Indefinite)) return reuse();
      if (!nextIn("aA",$Term0)) return reject(Indefinite);
      nextIn("nN",$Term7);
      return accept(Indefinite);
    }
  
  //=====================================================================
  //  Pronoun = It / Them / Me ;
  //=====================================================================
  private boolean Pronoun()
    {
      if (saved(Pronoun)) return reuse();
      if (It()) return accept(Pronoun);
      if (Them()) return accept(Pronoun);
      if (Me()) return accept(Pronoun);
      return reject(Pronoun);
    }
  
  //=====================================================================
  //  It = [iI] [tT] ;
  //=====================================================================
  private boolean It()
    {
      if (saved(It)) return reuse();
      if (!nextIn("iI",$Term9)) return reject(It);
      if (!nextIn("tT",$Term12)) return reject(It);
      return accept(It);
    }
  
  //=====================================================================
  //  Them = [tT] [hH] [eE] [mM] ;
  //=====================================================================
  private boolean Them()
    {
      if (saved(Them)) return reuse();
      if (!nextIn("tT",$Term12)) return reject(Them);
      if (!nextIn("hH",$Term4)) return reject(Them);
      if (!nextIn("eE",$Term6)) return reject(Them);
      if (!nextIn("mM",$Term2)) return reject(Them);
      return accept(Them);
    }
  
  //=====================================================================
  //  Me = [mM] [eE] ;
  //=====================================================================
  private boolean Me()
    {
      if (saved(Me)) return reuse();
      if (!nextIn("mM",$Term2)) return reject(Me);
      if (!nextIn("eE",$Term6)) return reject(Me);
      return accept(Me);
    }
  
  //=====================================================================
  //  EOT = !_ ;
  //=====================================================================
  private boolean EOT()
    {
      if (saved(EOT)) return reuse();
      if (!aheadNot($Term14)) return reject(EOT);
      return accept(EOT);
    }
  
  //=======================================================================
  //
  //  Cache objects
  //
  //=======================================================================
  
  final Cache Period = new Cache("Period","Period");
  final Cache Sentence = new Cache("Sentence","Sentence");
  final Cache Say = new Cache("Say","Say");
  final Cache Tell = new Cache("Tell","Tell");
  final Cache Verb = new Cache("Verb","Verb");
  final Cache Name = new Cache("Name","Name");
  final Cache Complement = new Cache("Complement","Complement");
  final Cache Preposition = new Cache("Preposition","Preposition");
  final Cache Nominal = new Cache("Nominal","Nominal");
  final Cache Descriptor = new Cache("Descriptor","Descriptor");
  final Cache Genitive = new Cache("Genitive","Genitive");
  final Cache Speech = new Cache("Speech","Speech");
  final Cache Word = new Cache("Word","Word");
  final Cache Space = new Cache("Space","Space");
  final Cache Then = new Cache("Then","Then");
  final Cache Conjunction = new Cache("Conjunction","Conjunction");
  final Cache And = new Cache("And","And");
  final Cache Comma = new Cache("Comma","Comma");
  final Cache Article = new Cache("Article","Article");
  final Cache Definite = new Cache("Definite","Definite");
  final Cache Indefinite = new Cache("Indefinite","Indefinite");
  final Cache Pronoun = new Cache("Pronoun","Pronoun");
  final Cache It = new Cache("It","It");
  final Cache Them = new Cache("Them","Them");
  final Cache Me = new Cache("Me","Me");
  final Cache EOT = new Cache("EOT","EOT");
  
  final Cache Period_0 = new Cache("Period_0"); // Space Then Space Sentence
  final Cache Sentence_0 = new Cache("Sentence_0"); // Say Space Speech
  final Cache Sentence_1 = new Cache("Sentence_1"); // Tell Space Name Space Speech
  final Cache Sentence_2 = new Cache("Sentence_2"); // Verb (Space Complement)*
  final Cache Sentence_3 = new Cache("Sentence_3"); // Space Complement
  final Cache Complement_0 = new Cache("Complement_0"); // (Preposition Space)? Nominal (Space Conjunction Space Nominal)*
  final Cache Complement_1 = new Cache("Complement_1"); // ":" Speech
  final Cache Complement_2 = new Cache("Complement_2"); // Preposition Space
  final Cache Complement_3 = new Cache("Complement_3"); // Space Conjunction Space Nominal
  final Cache Nominal_0 = new Cache("Nominal_0"); // (Article Space)? Descriptor (Space Descriptor)*
  final Cache Nominal_1 = new Cache("Nominal_1"); // Article Space
  final Cache Nominal_2 = new Cache("Nominal_2"); // Space Descriptor
  final Cache Descriptor_0 = new Cache("Descriptor_0"); // Word Genitive?
  final Cache Descriptor_1 = new Cache("Descriptor_1"); // Preposition Space Descriptor
  final Cache Speech_0 = new Cache("Speech_0"); // _++ EOT
  final Cache Speech_1 = new Cache("Speech_1"); // """ _++ (""" / EOT)
  final Cache Speech_2 = new Cache("Speech_2"); // "'" _++ ("'" / EOT)
  final Cache Speech_3 = new Cache("Speech_3"); // """ / EOT
  final Cache Speech_4 = new Cache("Speech_4"); // "'" / EOT
  
  final Cache $Term0 = new Cache("[aA]");
  final Cache $Term1 = new Cache("\"\"\"");
  final Cache $Term2 = new Cache("[mM]");
  final Cache $Term3 = new Cache("\"'\"");
  final Cache $Term4 = new Cache("[hH]");
  final Cache $Term5 = new Cache("[dD]");
  final Cache $Term6 = new Cache("[eE]");
  final Cache $Term7 = new Cache("[nN]");
  final Cache $Term8 = new Cache("\":\"");
  final Cache $Term9 = new Cache("[iI]");
  final Cache $Term10 = new Cache("_");
  final Cache $Term11 = new Cache("_");
  final Cache $Term12 = new Cache("[tT]");
  final Cache $Term13 = new Cache("\",\"");
  final Cache $Term14 = new Cache("_");
  final Cache $Term15 = new Cache("^[ \t\n\r]");
  final Cache $Term16 = new Cache("[ \t]");
  final Cache $Term17 = new Cache("\"'s\"");
  
  //-------------------------------------------------------------------
  //  List of Cache objects
  //-------------------------------------------------------------------
  
  Cache[] cacheList =
  {
    Period,Sentence,Say,Tell,Verb,Name,Complement,Preposition,Nominal,
    Descriptor,Genitive,Speech,Word,Space,Then,Conjunction,And,Comma,
    Article,Definite,Indefinite,Pronoun,It,Them,Me,EOT,Period_0,
    Sentence_0,Sentence_1,Sentence_2,Sentence_3,Complement_0,
    Complement_1,Complement_2,Complement_3,Nominal_0,Nominal_1,
    Nominal_2,Descriptor_0,Descriptor_1,Speech_0,Speech_1,Speech_2,
    Speech_3,Speech_4,$Term0,$Term1,$Term2,$Term3,$Term4,$Term5,
    $Term6,$Term7,$Term8,$Term9,$Term10,$Term11,$Term12,$Term13,
    $Term14,$Term15,$Term16,$Term17
  };
}
