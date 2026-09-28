package co.edu.unicauca.presentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionService;

import co.unicauca.iso2.taller2.users.domain.User;
import co.unicauca.iso2.taller2.users.domain.Role;


public class GUIQuestionController {

    private GUIQuestion view;
    private QuestionService service;
    private User currentUser;

  

    public GUIQuestionController(
            GUIQuestion view,
            QuestionService service) {

        this(view, service, null);
    }

    public GUIQuestionController(
            GUIQuestion view,
            QuestionService service,
            User currentUser) {

        this.view = view;
        this.service = service;
        this.currentUser = currentUser;

        view.addLoadQuestionListener(
                new LoadQuestionListener()
        );

        view.addUpdateStatusListener(
                new UpdateStatusListener()
        );

        loadQuestions();
    
        view.setUpdateStatusEnabled(
        canUpdateStatus()
    );
    
    }


    private boolean canUpdateStatus() {

    if (currentUser == null) {
        return true;
    }

    Role role = currentUser.getRole();

    return role == Role.ADMINISTRADOR
            || role == Role.AUTOR_PREGUNTAS
            || role == Role.REVISOR;
    }


    private void loadQuestions() {

        var questions = service.getQuestions();

        String[] questionNames =
                new String[questions.size()];

        for (int i = 0; i < questions.size(); i++) {

            questionNames[i] =
                    questions.get(i).getName();
        }

        view.setQuestions(questionNames);
    }

    private class LoadQuestionListener
            implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String selectedQuestion =
                    view.getSelectedQuestion();

            if (selectedQuestion == null) {
                return;
            }

            for (Question question :
                    service.getQuestions()) {

                if (question.getName()
                        .equals(selectedQuestion)) {

                    String options =
                            "A. "
                            + question.getDistractors().getOptionA()
                            + "\nB. "
                            + question.getDistractors().getOptionB()
                            + "\nC. "
                            + question.getDistractors().getOptionC()
                            + "\nD. "
                            + question.getDistractors().getOptionD();

                    view.showQuestion(
                            question.getId(),
                            question.getName(),
                            question.getQuestion(),
                            options,
                            question.getCorrectAnswer(),
                            question.getStatus()
                    );

                    break;
                }
            }
        }
    }

    private class UpdateStatusListener
            implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String selectedQuestion =
                    view.getSelectedQuestion();

            String newStatus =
                    view.getSelectedStatus();

            if (selectedQuestion == null
                    || newStatus == null) {

                return;
            }

            for (Question question :
                    service.getQuestions()) {

                if (question.getName()
                        .equals(selectedQuestion)) {

                    service.updateQuestionStatus(
                            question.getId(),
                            newStatus
                    );

                    break;
                }
            }
        }
    }
}

