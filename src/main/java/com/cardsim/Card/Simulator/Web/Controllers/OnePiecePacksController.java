package com.cardsim.Card.Simulator.Web.Controllers;

import com.cardsim.Card.Simulator.Web.Service.FirebaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/packs/one-piece")
public class OnePiecePacksController {

    private final FirebaseService firebaseService;

    @Autowired
    public OnePiecePacksController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @GetMapping("/open-pack")
    public HashMap<String, HashMap<String, Object>> openPack(@RequestParam String expansionName)
            throws ExecutionException, InterruptedException {
        return this.firebaseService.pullOnePieceCards(expansionName);
    }
}
