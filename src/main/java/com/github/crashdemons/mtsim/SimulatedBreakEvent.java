/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.mtsim;

import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class SimulatedBreakEvent extends BlockBreakEvent {
    public static long count=0;
    public volatile long id;
    
    
    public SimulatedBreakEvent(BlockBreakEvent evt){
        super(evt.getBlock(),evt.getPlayer());
        assignId();
    }
    
    private synchronized void assignId(){
        this.id=count;
        count++;
    }
}
