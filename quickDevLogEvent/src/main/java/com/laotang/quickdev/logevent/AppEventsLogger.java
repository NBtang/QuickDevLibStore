package com.laotang.quickdev.logevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;

import static java.util.Collections.unmodifiableList;

public final class AppEventsLogger {

    public static void logEvent(String eventName, Bundle params) {
        TREE_OF_SOULS.log(eventName, params);
    }

    public static void plant(Tree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant Timber into itself.");
        }
        synchronized (FOREST) {
            FOREST.add(tree);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    public static void plant(Tree... trees) {
        if (trees == null) {
            throw new NullPointerException("trees == null");
        }
        for (Tree tree : trees) {
            if (tree == null) {
                throw new NullPointerException("trees contains null");
            }
            if (tree == TREE_OF_SOULS) {
                throw new IllegalArgumentException("Cannot plant Timber into itself.");
            }
        }
        synchronized (FOREST) {
            Collections.addAll(FOREST, trees);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /** Remove a planted tree. */
    public static void uproot(Tree tree) {
        synchronized (FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
            }
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /** Remove all planted trees. */
    public static void uprootAll() {
        synchronized (FOREST) {
            FOREST.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }
    }

    /** Return a copy of all planted {@linkplain Tree trees}. */
    public static List<Tree> forest() {
        synchronized (FOREST) {
            return unmodifiableList(new ArrayList<>(FOREST));
        }
    }

    public static int treeCount() {
        synchronized (FOREST) {
            return FOREST.size();
        }
    }

    private static final Tree[] TREE_ARRAY_EMPTY = new Tree[0];
    // Both fields guarded by 'FOREST'.
    private static final List<Tree> FOREST = new ArrayList<>();
    private static volatile Tree[] forestAsArray = TREE_ARRAY_EMPTY;


    private static final Tree TREE_OF_SOULS = new Tree() {

        @Override
        protected void log(String eventName, Bundle params) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.logEvent(eventName, params);
            }
        }
    };

    private AppEventsLogger() {
        throw new AssertionError("No instances.");
    }

    public static abstract class Tree {

        public void logEvent(String eventName, Bundle params) {
            prepareLog(eventName, params);
        }

        private void prepareLog(String eventName, Bundle params) {
            if (!isLoggable(eventName)) {
                return;
            }
            log(eventName, params);
        }

        protected boolean isLoggable(String eventName) {
            return true;
        }

        protected abstract void log(String eventName, Bundle params);
    }
}
