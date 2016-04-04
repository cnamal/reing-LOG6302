class WordCount {

    static final String text = "Cycle de re-ingénierie : re-ingenierie, restructuration, \n" +
        "retro-ingenierie, recuperation de la conception \n" +
        "(Design Recovery) et re-documentation.\n";

    static final int len = text.length();

    static public void main (String args[]) {
        boolean inword = false;
        int nl = 0;
        int nw = 0;
        int nc = 0;
        for (int i = 0; i<len; i++) {
            final char c = text.charAt(i);
            nc=nc+1;
            if( c == '\n')
                nl=nl+1;
            if( c == ' ' || c == '\n' || c == '\t')
                inword = false;
            else if( inword == false ){
                inword = true;
                nw++;
            }
        }

        System.out.println ( nl);
        System.out.println ( nw);
        System.out.println ( nc);
    }
}
