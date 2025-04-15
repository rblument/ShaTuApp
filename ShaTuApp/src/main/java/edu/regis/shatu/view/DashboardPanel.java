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

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.aol.ViewType;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * The dashboard screen to be displayed upon user sign in. Enables user to
 * select a mode from the tutor (teach me, practice, quiz me) Tracks user's
 * progress for each mode and learning objective.
 *
 * @author Ryley MacLagan, Rickb, Cameron Brucher
 */
public class DashboardPanel extends JPanel {

    private TutoringSession model; // Reference to current tutoringSession
    private static boolean welcome = false;
    
    private JButton logOutButton;
    private JButton settingsButton;
    private JButton teachMeButton;
    private JButton practiceButton;
    private JButton quizMeButton;
    private JLabel welcomeLabel;
    
    // Panels to hold progress bars for each study mode (will be added to scroll panes)
    private JPanel teachMePanel;
    private JPanel practicePanel;
    private JPanel quizMePanel;
    
    // Maps for individual progress bars by lesson name
    private HashMap<String, JProgressBar> teachMeProgressBars;
    private HashMap<String, JProgressBar> practiceProgressBars;
    private HashMap<String, JProgressBar> quizMeProgressBars;
    
    // List of all lesson titles from the DB (excluding IDs 0, 10, 20)
    private List<String> allLessons = new ArrayList<>();
    
    // Database URL (used by service/DAO layer)
    private static final String URL = "jdbc:mysql://localhost:3306/shatudb?serverTimezone=UTC";

    /**
     * Creates a new DashboardPanel and initializes components.
     *
     * @param tutoringSession the current tutoring session.
     */
    public DashboardPanel(TutoringSession tutoringSession) {
        this.model = tutoringSession;
        
        if (welcome == false) {
            welcome = true;
            String welcomeMessage = "Welcome, " 
                    + tutoringSession.getStudent().getAccount().getFirstName() + "! Your session has successfully started.";
            JOptionPane.showMessageDialog(null, welcomeMessage, "Welcome", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Load lessons and set up UI components
        loadAllLessons();
        initializeComponents();
        layoutComponents();
    }
    
    /**
     * Updates the tutoring session model.
     *
     * @param model the new tutoring session model.
     */
    public void setModel(TutoringSession model) {
        this.model = model;
    }
    
    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        // Header components
        logOutButton = new JButton("Log Out");
        settingsButton = new JButton("Settings");
        welcomeLabel = new JLabel();
        welcomeLabel.setBackground(new Color(0, 43, 73));
        welcomeLabel.setOpaque(true);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomeLabel.setForeground(new Color(241, 196, 0));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Study mode buttons
        teachMeButton = new JButton("Teach Me");
        practiceButton = new JButton("Practice");
        quizMeButton = new JButton("Quiz Me");
        
        // Set button actions and update scaffold level based on chosen study mode.
        logOutButton.addActionListener(evt -> logOut());
        settingsButton.setVerticalAlignment(SwingConstants.TOP);

        teachMeButton.addActionListener(evt -> {
            // Set scaffold level to EXTREME when in Teach Me mode.
            try {
                ServiceFactory.findStudentModelSvc().updateScaffoldLevel(
                        model.getStudent().getAccount().getUserId(),
                        ScaffoldLevel.EXTREME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Navigate to the view for Teach Me mode using ViewType.SEE_ONE
            SplashFrame.instance().selectScreen(ViewType.SEE_ONE);
        });

        practiceButton.addActionListener(evt -> {
            // Set scaffold level to HIGH when in Practice mode.
            try {
                ServiceFactory.findStudentModelSvc().updateScaffoldLevel(
                        model.getStudent().getAccount().getUserId(),
                        ScaffoldLevel.MEDIUM);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Navigate to the view for Practice mode using ViewType.DO_ONE
            SplashFrame.instance().selectScreen(ViewType.DO_ONE);
        });

        quizMeButton.addActionListener(evt -> {
            // Set scaffold level to MEDIUM when in Quiz Me mode.
            try {
                ServiceFactory.findStudentModelSvc().updateScaffoldLevel(
                        model.getStudent().getAccount().getUserId(),
                        ScaffoldLevel.NONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Navigate to the view for Quiz Me mode using ViewType.TEACH_ONE
            SplashFrame.instance().selectScreen(ViewType.TEACH_ONE);
        });
        
        // Initialize progress bar maps and panels
        teachMeProgressBars = new HashMap<>();
        practiceProgressBars = new HashMap<>();
        quizMeProgressBars = new HashMap<>();
        teachMePanel = new JPanel(new GridBagLayout());
        practicePanel = new JPanel(new GridBagLayout());
        quizMePanel = new JPanel(new GridBagLayout());
    }
    
    /**
     * Lays out all UI components on the panel.
     */
    private void layoutComponents() {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setPreferredSize(new Dimension(986, 480));
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(settingsButton, BorderLayout.LINE_START);
        welcomeLabel.setText("Welcome, " + model.getStudent().getAccount().getFirstName() + "!");
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logOutButton, BorderLayout.LINE_END);
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(0, 43, 73));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Category panels with scroll panes
        JPanel teachMeCategoryPanel = createCategoryPanel("Teach Me");
        JScrollPane teachMeScroll = new JScrollPane(teachMeCategoryPanel);
        teachMeScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 0;
        gbc.gridx = 0;
        contentPanel.add(teachMeScroll, gbc);
        
        JPanel practiceCategoryPanel = createCategoryPanel("Practice");
        JScrollPane practiceScroll = new JScrollPane(practiceCategoryPanel);
        practiceScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridx = 1;
        contentPanel.add(practiceScroll, gbc);
        
        JPanel quizMeCategoryPanel = createCategoryPanel("Quiz Me");
        JScrollPane quizMeScroll = new JScrollPane(quizMeCategoryPanel);
        quizMeScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridx = 2;
        contentPanel.add(quizMeScroll, gbc);
        
        // Add study mode buttons below the panels
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        contentPanel.add(teachMeButton, gbc);
        gbc.gridx = 1;
        contentPanel.add(practiceButton, gbc);
        gbc.gridx = 2;
        contentPanel.add(quizMeButton, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads lesson titles from the service layer (excludes lessons with IDs 0, 10, 20).
     */
    private void loadAllLessons() {
        try {
            String userId = model.getStudent().getAccount().getUserId();
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();
            //allLessons = studentModelService.retrieveAllLessons(userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lessons: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            allLessons = new ArrayList<>();
        }
    }
    
    /**
     * Shortens a lesson title to a maximum of 20 characters.
     *
     * @param lesson the original lesson title.
     * @return the shortened lesson title.
     */
    private String truncateLessonTitle(String lesson) {
        if (lesson.length() <= 23) {
            return lesson;
        }
        int lastSpace = lesson.lastIndexOf(" ", 20);
        if (lastSpace == -1) {
            return lesson.substring(0, 20) + "...";
        } else {
            return lesson.substring(0, lastSpace) + "...";
        }
    }
    
    /**
     * Converts an AssessmentLevel to progress percentages for [Teach Me, Practice, Quiz Me].
     *
     * Mapping:
     *   NOT_STARTED: [0, 0, 0]
     *   VERY_LOW:    [50, 0, 0]
     *   LOW:         [100, 0, 0]
     *   MEDIUM:      [100, 50, 0]
     *   HIGH:        [100, 100, 0]
     *   VERY_HIGH:   [100, 100, 50]
     *   COMPLETED:   [100, 100, 100]
     *
     * @param level the assessment level.
     * @return an array with progress percentages.
     */
    private int[] mapAssessmentToProgress(AssessmentLevel level) {
        switch (level) {
            case NOT_STARTED: return new int[]{0, 0, 0};
            case VERY_LOW:    return new int[]{50, 0, 0};
            case LOW:         return new int[]{100, 0, 0};
            case MEDIUM:      return new int[]{100, 50, 0};
            case HIGH:        return new int[]{100, 100, 0};
            case VERY_HIGH:   return new int[]{100, 100, 50};
            case COMPLETED:   return new int[]{100, 100, 100};
            default:          return new int[]{0, 0, 0};
        }
    }
    
    /**
     * Gets the progress percentage for a lesson in a given study mode.
     *
     * @param studyMode the study mode (e.g., "Teach Me").
     * @param lesson the lesson name.
     * @return the progress percentage.
     */
    private int getProgressForLesson(String studyMode, String lesson) {
        try {
            String userId = model.getStudent().getAccount().getUserId();
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();
            AssessmentLevel level = studentModelService.retrieveAssessmentLevel(userId, lesson);
            int[] progress = mapAssessmentToProgress(level);
            if ("Teach Me".equalsIgnoreCase(studyMode)) {
                return progress[0];
            } else if ("Practice".equalsIgnoreCase(studyMode)) {
                return progress[1];
            } else if ("Quiz Me".equalsIgnoreCase(studyMode)) {
                return progress[2];
            }
        } catch(Exception e) {
            // Handle error silently
        }
        return 0;
    }
    
    /**
     * Creates a panel for a study mode category.
     *
     * The panel shows an overall progress bar and sections for lessons that are Not Started,
     * In Progress, and Completed.
     *
     * @param category the study mode category.
     * @return the category panel.
     */
    private JPanel createCategoryPanel(String category) {
        List<String> notStarted = new ArrayList<>();
        List<String> inProgress = new ArrayList<>();
        List<String> completed = new ArrayList<>();
        
        for (String lesson : allLessons) {
            int progress = getProgressForLesson(category, lesson);
            if (progress == 0) {
                notStarted.add(lesson);
            } else if (progress == 50) {
                inProgress.add(lesson);
            } else if (progress == 100) {
                completed.add(lesson);
            }
        }
        
        if (notStarted.isEmpty()) { notStarted.add("None"); }
        if (inProgress.isEmpty()) { inProgress.add("None"); }
        if (completed.isEmpty()) { completed.add("None"); }
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(241, 196, 0)); // Yellow background
        panel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        
        // Category header
        JLabel header = new JLabel(category + " Progress");
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(header, gbc);
        
        // Overall progress bar (average progress)
        int sum = 0;
        for (String lesson : allLessons) {
            sum += getProgressForLesson(category, lesson);
        }
        int overall = allLessons.isEmpty() ? 0 : sum / allLessons.size();
        JProgressBar overallBar = new JProgressBar(0, 100);
        overallBar.setValue(overall);
        overallBar.setStringPainted(true);
        overallBar.setPreferredSize(new Dimension(70, 35));
        overallBar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        overallBar.setForeground(new Color(0, 43, 73));
        overallBar.setBackground(Color.WHITE);
        overallBar.setString(overall + "%");
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(overallBar, gbc);
        
        gbc.gridwidth = 1;
        int row = 2;
        row = addSection(panel, row, "Not Started:", notStarted, "Teach Me", category);
        row = addSection(panel, row, "In Progress:", inProgress, "Practice", category);
        row = addSection(panel, row, "Completed:", completed, "Quiz Me", category);
        
        // Filler to push content to the top
        gbc.gridy = ++row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setBackground(new Color(241, 196, 0));
        panel.add(filler, gbc);
        
        panel.revalidate();
        panel.repaint();
        return panel;
    }
    
    /**
     * Adds a section (Not Started, In Progress, or Completed) to the category panel.
     *
     * For each lesson, it adds a label (with a shortened title) and a progress bar.
     *
     * @param panel the parent panel.
     * @param startRow the starting row index.
     * @param sectionName the section title.
     * @param lessons the list of lessons in this section.
     * @param expectedCategory the study mode for fetching progress.
     * @param studyMode the current study mode.
     * @return the next row index.
     */
    private int addSection(JPanel panel, int startRow, String sectionName,
                           List<String> lessons, String expectedCategory, String studyMode) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        
        int row = startRow;
        // Section header label
        JLabel secLabel = new JLabel(sectionName);
        secLabel.setForeground(Color.BLACK);
        secLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(secLabel, gbc);
        gbc.gridwidth = 1;
        
        // Add each lesson with its progress bar
        for (String lesson : lessons) {
            gbc.gridx = 0;
            gbc.gridy = row;
            String displayLesson = truncateLessonTitle(lesson);
            JLabel lessonLabel = new JLabel(displayLesson);
            lessonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lessonLabel.setForeground(Color.BLACK);
            lessonLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
            panel.add(lessonLabel, gbc);
            
            if (!"None".equals(lesson)) {
                gbc.gridx = 1;
                JProgressBar bar = new JProgressBar(0, 100);
                int progress = getProgressForLesson(studyMode, lesson);
                bar.setValue(progress);
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(35, 25));
                bar.setFont(new Font("Segoe UI", Font.BOLD, 16));
                bar.setForeground(new Color(0, 43, 73));
                bar.setBackground(Color.WHITE);
                bar.setString(progress + "%");
                panel.add(bar, gbc);
            }
            row++;
        }
        
        return row;
    }
    
    /**
     * Logs out the current user.
     */
    private void logOut() {
        SplashFrame.instance().logout();
    }
}
