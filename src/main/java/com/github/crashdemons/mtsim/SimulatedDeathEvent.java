/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.mtsim;

import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class SimulatedDeathEvent extends EntityDeathEvent {
    public static long count=0;
    public volatile long id;
    
    
    public SimulatedDeathEvent(EntityDeathEvent evt){
        super(evt.getEntity(),evt.getDrops());
        assignId();
    }
    
    private synchronized void assignId(){
        this.id=count;
        count++;
    }
}
