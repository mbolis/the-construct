//=========================================================================
//
//  This skeleton was generated by Mouse 1.7 at 2015-12-07 15:18:29 GMT
//  from grammar
//    '/home/developer/workspace-trunk/the-construct/parser/Commands.mouse'
//    .
//
//=========================================================================

class CommandActions extends mouse.runtime.SemanticsBase {
    // -------------------------------------------------------------------
    // Say = Word
    // -------------------------------------------------------------------
    boolean Say() {
        if ("say".equalsIgnoreCase(rhs(0).text())) {
            System.out.println("SAY");
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------
    // Tell = Word
    // -------------------------------------------------------------------
    boolean Tell() {
        if ("tell".equalsIgnoreCase(rhs(0).text())) {
            System.out.println("TELL");
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------
    // Verb = Word
    // -------------------------------------------------------------------
    boolean Verb() {
        System.out.println("VERB[ " + rhs(0).text() + " ]");
        return true;
    }

    // -------------------------------------------------------------------
    // Name = Word
    // -------------------------------------------------------------------
    boolean Name() {
        return true;
    }

    // -------------------------------------------------------------------
    // Complement = (Preposition Space)? Nominal (Space Conjunction
    // Space Nominal)*
    // -------------------------------------------------------------------
    void Complement_0() {
    }

    // -------------------------------------------------------------------
    // Complement = ":" Speech
    // -------------------------------------------------------------------
    void Complement_1() {
    }

    // -------------------------------------------------------------------
    // Preposition = Word
    // -------------------------------------------------------------------
    boolean Preposition() {
        return true;
    }

    // -------------------------------------------------------------------
    // Nominal = (Article Space)? Descriptor (Space Descriptor)*
    // -------------------------------------------------------------------
    void Nominal_0() {
    }

    // -------------------------------------------------------------------
    // Nominal = Pronoun
    // -------------------------------------------------------------------
    void Nominal_1() {
    }

    // -------------------------------------------------------------------
    // Descriptor = Word Genitive?
    // -------------------------------------------------------------------
    void Descriptor_0() {
    }

    // -------------------------------------------------------------------
    // Descriptor = Preposition Space Descriptor
    // -------------------------------------------------------------------
    void Descriptor_1() {
    }

}
