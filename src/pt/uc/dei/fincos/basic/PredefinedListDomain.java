/* FINCoS Framework
 * Copyright (C) 2013 CISUC, University of Coimbra
 *
 * Licensed under the terms of The GNU General Public License, Version 2.
 * A copy of the License has been included with this distribution in the
 * fincos-license.txt file.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 */


package pt.uc.dei.fincos.basic;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

/**
 * A Domain that generates items from a predefined list of items.
 *
 * @author  Marcelo R.N. Mendes
 *
 * @see Domain
 * @see RandomDomain
 * @see SequentialDomain
 *
 */
public final class PredefinedListDomain extends Domain {
    /** serial id. */
    private static final long serialVersionUID = -3816217657821868946L;

    /** A list of possible values for this domain and their corresponding frequencies. */
    private LinkedHashMap<Object, Double> itemMix;

    /** A list of possible values for this domain. */
    private Object[] items;

    /** Random number generator. */
    private Random rnd;

    /** Keeps the frequencies of each item as an interval in the [0,1) range. */
    private LinkedHashMap<? extends Object, Double> itemSetRanges;

    /** Keeps track of the last generated item. */
    private int index;

    /**
     * Creates a new domain with Deterministic behavior: the values are
     * generated by cycling through the list of items passed as argument.
     *
     * @param domainItems     A list of possible values for this domain
     */
    public PredefinedListDomain(final Object[] domainItems) {
        this.setItems(domainItems);
        this.index = 0;
    }

    /**
     * Creates a new domain with Stochastic behavior: the values are generated
     * randomly according  to their frequency (approximately).
     *
     * (NOTE: Frequencies do not need to sum up 1 (or 100), because they are
     * normalized. That is, if two items have the same value for the "frequency"
     * parameter, they will be generated in (approximately) the same proportion,
     * no matter the scale of the "frequency" parameter.
     *
     * @param itemMix       A mapping item->frequency
     * @param randomSeed    Seed for random number generation
     */
    public PredefinedListDomain(LinkedHashMap<Object, Double> itemMix,
            final Long randomSeed) {
        if (randomSeed != null) {
            this.rnd = new Random(randomSeed);
        } else {
            this.rnd = new Random();
        }

        this.itemMix = new LinkedHashMap<Object, Double>(itemMix.size());
        this.itemMix.putAll(itemMix);
        this.setItemSetRanges(itemMix);
    }

    /**
     *
     * @return  <code>true</code> if this domains generates values in a ordered,
     *          predictable way; <code>false</code> otherwise.
     */
    public boolean isDeterministic() {
        return (this.items != null);
    }


    @Override
    public Object generateValue() {
        if (this.items != null) {
            synchronized (this) {
                Object item = this.items[index];
                index = (++index) % this.items.length;
                return item;
            }
        } else {
            if (this.itemSetRanges != null) {
                double number = rnd.nextDouble();
                Object key = "";
                for (Entry<? extends Object, Double> e : this.itemSetRanges.entrySet()) {
                    if (number < e.getValue()) {
                        key = e.getKey();
                        break;
                    }
                }
                return key;
            } else {
                return null;
            }
        }
    }

    /**
     * Normalizes the frequencies, so that it is not required that
     * frequencies sum up 1 and distributes the items in the range [0,1).
     *
     * @param itemMix   the items frequencies
     */
    private void setItemSetRanges(LinkedHashMap<Object, Double> itemMix) {
        if (itemMix != null) {
            double total = 0;

            Iterator<Double> freqIter = itemMix.values().iterator();
            while (freqIter.hasNext()) {
                total += freqIter.next();
            }

            Iterator<Entry<Object, Double>> setValueIter =
                itemMix.entrySet().iterator();
            Entry<Object, Double> e;
            Double value;
            Double previousValue = 0.0;
            while (setValueIter.hasNext()) {
                e = setValueIter.next();
                value = previousValue + e.getValue() / total;
                previousValue = value;
                itemMix.put(e.getKey(), value);
            }

            this.itemSetRanges = itemMix;
        } else {
            System.err.println("WARNING: PredefinedListDomain's itemset"
                             + " was set to null.");
        }
    }

    /**
     * Sets the possible values generated by this domain.
     *
     * @param its     A list of possible values for this domain
     */
    private void setItems(final Object[] its) {
        this.items = its;
    }

    /**
     *
     * @return  A list of possible values generated by this domain
     */
    public Object[] getItems() {
        return items;
    }

    /**
     *
     * @return  A list of possible values for this domain and their
     *          corresponding frequencies.
     */
    public LinkedHashMap<Object, Double> getItemMix() {
        return itemMix;
    }

    @Override
    public String toString() {
        return "Predefined list";
    }

    @Override
    public void setRandomSeed(final Long seed) {
        if (this.rnd != null && seed != null) {
            this.rnd.setSeed(seed);
        } else {
            if (seed != null) {
                this.rnd = new Random(seed);
            } else {
                this.rnd = new Random();
            }
        }
    }
}
