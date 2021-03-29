package com.vaneks.crud.controller;

import com.vaneks.crud.model.Developer;
import com.vaneks.crud.model.Team;
import com.vaneks.crud.model.TeamStatus;
import com.vaneks.crud.repository.DeveloperRepository;
import com.vaneks.crud.repository.TeamRepository;
import com.vaneks.crud.repository.io.JsonDeveloperRepositoryImpl;
import com.vaneks.crud.repository.io.JsonTeamRepositoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamController {
    TeamRepository controllerTeam = JsonTeamRepositoryImpl.getJsonTeamRepository();
    DeveloperRepository developerRepository = JsonDeveloperRepositoryImpl.getJsonDeveloperRepository();;

    public List<Team> ControllerAll() {
        return controllerTeam.getAll();
    }

    public Team controllerId(String id) {
        return controllerTeam.getById(strToLong(id));
    }

    public Team controllerUpdate(String id, String[] developers) {
        try {
            return controllerTeam.update(new Team(TeamStatus.UNDER_REVIEW,strToLong(id), controllerTeam.getById(strToLong(id)).getName(), developerCount(developers)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void controllerDeleteById(String id) {
        try {
            controllerTeam.deleteById(strToLong(id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void controllerDeleteAll() {
        try {
            controllerTeam.deleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Team controllerSave(String name, String[] developers) {
        try {
            return controllerTeam.save(new Team(TeamStatus.ACTIVE,null, name, developerCount(developers)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Developer> developerCount(String[] developers) {
        List<Developer> listDeveloper = new ArrayList<Developer>();
        for(int i = 0; i < developers.length ; i++) {
            listDeveloper.add(developerRepository.getById(strToLong(developers[i])));
        }
        return listDeveloper;
    }

    private Long strToLong(String string) {
        return Long.parseLong(string);
    }
}
