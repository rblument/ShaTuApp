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
package edu.regis.shatu.view;

/**
 *
 * @author mwemapowanga
 */
public class LessonGuiController {
    /**
     * The singleton instance of this controller.
     */
    private static LessonGuiController SINGLETON;
    
    static {
        SINGLETON = new LessonGuiController();
    }
    
    /**
     * Return the singleton instanced of this controller.
     * 
     * @return LessonGuiController
     */

    public static LessonGuiController instance() {
        return SINGLETON;
    }
    
    /**
     * A convenience reference to the step selector view.
     */
    private LessonStepSelectorView lessonStepSelectorView;
    
    /**
     * A convenience reference to the step view.
     */
    private LessonStepView lessonView;

    public LessonStepSelectorView getLessonStepSelectorView() {
        return lessonStepSelectorView;
    }

    public void setLessonStepSelectorView(LessonStepSelectorView lessonStepSelectionView) {
        this.lessonStepSelectorView = lessonStepSelectionView;
    }

    public LessonStepView getLessonView() {
        return lessonView;
    }

    public void setLessonView(LessonStepView lessonView) {
        this.lessonView = lessonView;
    }
    
}
