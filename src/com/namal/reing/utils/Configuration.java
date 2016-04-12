package com.namal.reing.utils;

import java.util.*;

/**
 * Created by namalgac on 4/3/16.
 */
public class Configuration {

    static Map<EConfiguration,Boolean> map = new HashMap<>();
    static EConfiguration TP;
    public static void setConfiguration(Set<EConfiguration> trueSettings){
        for(EConfiguration e : EConfiguration.values()){
            if(trueSettings.contains(e)) {
                map.put(e, true);
                if(e==EConfiguration.TP1|| e==EConfiguration.TP2||e==EConfiguration.TP5)
                    TP=e;
            }else
                map.put(e,false);
        }
    }

    public static EConfiguration getTP(){
        return TP;
    }

    public static Set<EConfiguration> getTP5Config(){
        Set<EConfiguration> configurations = new HashSet<>();

        if(map.get(EConfiguration.INOUT))
            configurations.add(EConfiguration.INOUT);
        if(map.get(EConfiguration.CFG))
            configurations.add(EConfiguration.CFG);
        if(map.get(EConfiguration.DOM))
            configurations.add(EConfiguration.DOM);
        if(map.get(EConfiguration.PDOM))
            configurations.add(EConfiguration.PDOM);
        if(map.get(EConfiguration.CDG))
            configurations.add(EConfiguration.CDG);
        if(map.get(EConfiguration.DDG))
            configurations.add(EConfiguration.DDG);
        if(map.get(EConfiguration.PDG))
            configurations.add(EConfiguration.PDG);
        if(map.get(EConfiguration.SLICE))
            configurations.add(EConfiguration.SLICE);
        if (map.get(EConfiguration.TEST))
            configurations.add(EConfiguration.TEST);
        if (map.get(EConfiguration.SLICEALL))
            configurations.add(EConfiguration.SLICEALL);
        return configurations;
    }
}
