package com.example.Yrkeskompassen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.prefs.Preferences;

@Controller
public class KompassController {
    @Autowired
    QuestionRepository repository;
    @Autowired
    ProfessionRepository professionRepository;

    @Autowired
    KompassService service;


    @GetMapping("/")
    public String home(HttpSession session) {
        List<Traits> traitsList = new ArrayList<>();
        session.setAttribute("traitsList", traitsList);
        return "home";
    }
    @GetMapping("/start")
    public String start(HttpSession session) {
        List<Traits> traitsList = new ArrayList<>();
        session.setAttribute("traitsList", traitsList);
        return "redirect:/question/" + 1l;
    }
    @GetMapping("/question/{id}")
    public String current (@PathVariable Long id, Model model, HttpSession session){

        Questions question = repository.findById(id).get();

        List<Questions> q = (List)repository.findAll();

        model.addAttribute("questionSize", q.size());

        model.addAttribute("currentQuestion", id);

        session.setAttribute("question", question);

        List<Traits> traits = new ArrayList<>();
        session.setAttribute("traits", traits);

        return "Start";

    }

    @PostMapping("/result/{id}")
    public String next (@PathVariable Long id, Model model, @RequestParam (required = false) Boolean action, HttpSession session) {
        if (action == null){
            return "redirect:/question/" + (id);
        }
        model.getAttribute("currentQuestion");

        session.getAttribute("question");
        session.getAttribute("traits");

        session.getAttribute("traitsList");
        List <Traits> traitsList = (List)session.getAttribute("traitsList");

        if (action) {
            Questions currentQuestion = (Questions) session.getAttribute("question");
            currentQuestion.setAnswer(true);
            repository.save(currentQuestion);

            traitsList = service.addPointsOrNewTrait(traitsList, currentQuestion);
        }

        List<Profession> matchedList = new ArrayList<>();
        List<Questions> qs = (List)repository.findAll();
        if (id == qs.size()) {
            // kolla traitsList mot professionRepo
            List<Profession> professionList =(List)professionRepository.findAll();

            matchedList = service.matchTraitsAndProfession(professionList, traitsList);

            Collections.sort(traitsList, new SortTraits().reversed());

            model.addAttribute("matchedList", matchedList);
            return "result";
        }

        return "redirect:/question/" + (id + 1l);
    }

    @GetMapping("/result/{id}")
    public String resultPage(Model model, HttpSession session, Long id) {
        model.getAttribute("matchedList");
        session.getAttribute("traitsList");

        return "result";
    }
}
