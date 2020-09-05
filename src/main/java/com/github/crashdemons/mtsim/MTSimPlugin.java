/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.mtsim;

import com.github.crashdemons.playerheads.api.PlayerHeads;
import com.github.crashdemons.playerheads.api.PlayerHeadsAPI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.shininet.bukkit.playerheads.events.HeadRollEvent;
import org.shininet.bukkit.playerheads.events.LivingEntityDropHeadEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MTSimPlugin extends JavaPlugin implements Listener{
    
    PlayerHeadsAPI ph;
    
    private final Object testingLock = new Object();
    private boolean testing=false;
    
    private final AtomicInteger deathsBefore = new AtomicInteger(0);
    private final AtomicInteger successesBefore= new AtomicInteger(0);
    private final AtomicInteger deathsAfter= new AtomicInteger(0);
    private final AtomicInteger successesAfter= new AtomicInteger(0);
    Queue<Double> originalRates = new ConcurrentLinkedQueue<Double>();
    Queue<Double> effectiveRates = new ConcurrentLinkedQueue<Double>();
    Queue<Double> originalRolls = new ConcurrentLinkedQueue<Double>();
    Queue<Double> effectiveRolls = new ConcurrentLinkedQueue<Double>();
    
    //----------------------------------------------------
    void setTesting(boolean state){
       synchronized(testingLock){
           testing = state;
           if(testing){
                onTestingStart();
           }else{
                onTestingFinish();
           }
       }
    }
    boolean getTesting(){
       synchronized(testingLock){
           return testing;
       }
    }
    @EventHandler
    public void onSetTesting(SetTestingEvent event){
        setTesting(event.state);
    }
    //----------------------------------------------------
    
    public void onTestingCleanup(){
        deathsBefore.set(0);
        successesBefore.set(0);
        deathsAfter.set(0);
        successesAfter.set(0);
        
        originalRates.clear();
        effectiveRates.clear();
        originalRolls.clear();
        effectiveRolls.clear();
    }
    
    
    public void onTestingStart(){
        getLogger().info("Started testing!");
    }
    
    public void onTestingFinish(){
        getLogger().info("Finished testing!");
        Double successRateBefore = new Double(successesBefore.get()) / new Double(deathsBefore.get());
        Double successRateAfter = new Double(successesAfter.get()) / new Double(deathsAfter.get());
        
        
        Double originalRoll = originalRolls.stream().mapToDouble(a->a).average().orElse(Double.NaN);
        Double originalRate = originalRates.stream().mapToDouble(a->a).average().orElse(Double.NaN);
        Double effectiveRoll = effectiveRolls.stream().mapToDouble(a->a).average().orElse(Double.NaN);
        Double effectiveRate = effectiveRates.stream().mapToDouble(a->a).average().orElse(Double.NaN);
        
        getLogger().info("Final simulated statistics: (before and after luck)");
        getLogger().info("  deaths: "+deathsBefore.get()+" -> "+deathsAfter.get());
        getLogger().info("  successes: "+successesBefore.get()+" -> "+successesAfter.get());
        getLogger().info("  average success rate: "+successRateBefore+" -> "+successRateAfter);
        getLogger().info("  originalDropRoll average: "+originalRoll);
        getLogger().info("  originalDropRate average: "+originalRate);
        getLogger().info("  effectiveDropRoll average: "+effectiveRoll);
        getLogger().info("  effectiveDropRate average: "+effectiveRate);
        
        onTestingCleanup();
    }
    
    @Override
    public void onEnable(){
        ph = PlayerHeads.getApiInstance();
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHeadRollLow(HeadRollEvent event){
        if(getTesting()){
            //measure drops
            deathsBefore.incrementAndGet();
            if(event.succeeded()) successesBefore.incrementAndGet();
        }else{
            getLogger().info("non-sim START Head Roll: "+
                    " killer="+event.getKiller().getName()+
                    " target="+event.getTarget().getName()+
                    " success="+event.getDropSuccess()+
                    " droprateO="+event.getOriginalDropRate()+
                    " droprollO="+event.getOriginalDropRoll()+
                    " droprateE="+event.getEffectiveDropRate()+
                    " droprollE="+event.getEffectiveDropRoll()+
                    ""
            );
            //debug drops
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeadRollHigh(HeadRollEvent event){
        if(getTesting()){
            //measure drops
            deathsAfter.incrementAndGet();
            if(event.succeeded()) successesAfter.incrementAndGet();
            originalRates.add(event.getOriginalDropRate());
            originalRolls.add(event.getOriginalDropRoll());
            effectiveRates.add(event.getEffectiveDropRate());
            effectiveRolls.add(event.getEffectiveDropRoll());
        }else{
            getLogger().info("non-sim END Head Roll: "+
                    " killer="+event.getKiller().getName()+
                    " target="+event.getTarget().getName()+
                    " success="+event.getDropSuccess()+
                    " droprateO="+event.getOriginalDropRate()+
                    " droprollO="+event.getOriginalDropRoll()+
                    " droprateE="+event.getEffectiveDropRate()+
                    " droprollE="+event.getEffectiveDropRoll()+
                    ""
            );
            //debug drops
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeadDrop(LivingEntityDropHeadEvent event){
        if(getTesting()){
            //measure drops
            event.setCancelled(true);
        }else{
            //debug drops
            ItemStack stack = event.getDrop();
            int amount=0;
            if(stack!=null) amount = stack.getAmount();
            getLogger().info("non-sim Head Drop: "
                    + " beheadee="+event.getEntity().getName()
                    + " drops="+amount+
                    ""
            );
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event){
        if(event==null) return;
        if(event instanceof SimulatedDeathEvent) return;
        if(event.getEntity().getKiller()==null) return;
        if(getTesting()) return;
        
        //start head simulation from initial event
        startTestingSync(event);
    }
    
    private void startTestingUnsafe(EntityDeathEvent event){
        getLogger().info("queuing tests");
        setTesting(true); // OR syncCallEvent(new SetTestingEvent(true));
        for(long i=0L;i<1000000L;i++){
            SimulatedDeathEvent newEvent = simulateDeathEvent(event,1*20L);
        }
        syncCallEvent(new SetTestingEvent(false),2*20L);
        getLogger().info("done queuing tests");
    }
    private void syncOperation(Runnable task, long tickDelay){
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, task, tickDelay);
    }
    private void startTestingSync(EntityDeathEvent event){
        syncOperation( () -> {
            startTestingUnsafe(event);
        }, 3*20L);
    }
    
    
    private void syncCallEvent(Event event, long tickDelay){
        syncOperation( () -> {
            Bukkit.getPluginManager().callEvent(
                    event
            );
        }, tickDelay);
    }
    
    private SimulatedDeathEvent simulateDeathEvent(EntityDeathEvent originalEvent, long tickDelay){
        SimulatedDeathEvent newEvent = new SimulatedDeathEvent(originalEvent);
        syncCallEvent(newEvent,tickDelay);
        return newEvent;
    }
    
    
}
