package model;

import java.util.List;

public record Question(String question, List<Answer> answers) {
}