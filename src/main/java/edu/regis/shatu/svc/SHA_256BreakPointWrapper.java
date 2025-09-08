/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.svc;

/**
 *
 * @author Devin Morrison
 */
public class SHA_256BreakPointWrapper {

    private final SHA_256BreakPoint currentBreakpoint;
    private final int roundIndex;

    public SHA_256BreakPointWrapper(SHA_256BreakPoint step) {
        this.currentBreakpoint = step;
        this.roundIndex = -1;
    }

    public SHA_256BreakPointWrapper(SHA_256BreakPoint step, int roundIndex) {
        this.currentBreakpoint = step;
        this.roundIndex = roundIndex;
    }

    public boolean shouldBreakHere(SHA_256BreakPoint step) {
        return currentBreakpoint.equals(step);
    }

    public boolean shouldBreakHere(SHA_256BreakPoint step, int index) {
        return currentBreakpoint.equals(step) && roundIndex == index;
    }

    public SHA_256BreakPoint getCurrentBreakpoint() {
        return currentBreakpoint;
    }

    public int getRoundIndex() {
        return roundIndex;
    }
}

