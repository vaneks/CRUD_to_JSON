package com.vaneks.crud.repository.io;

import com.google.gson.Gson;
import com.vaneks.crud.model.Developer;
import com.vaneks.crud.repository.DeveloperRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JsonDeveloperRepositoryImpl implements DeveloperRepository {
    private final String file = "src/main/resources/developers.json";

    private static JsonDeveloperRepositoryImpl jsonDeveloperRepository;
    public static synchronized JsonDeveloperRepositoryImpl getJsonDeveloperRepository() {
        if(jsonDeveloperRepository == null) {
            jsonDeveloperRepository = new JsonDeveloperRepositoryImpl();
        }
        return jsonDeveloperRepository;
    }

    private JsonDeveloperRepositoryImpl(){}

    public List<Developer> getAll() {return getAllInternal();}

    public Developer getById(Long id) {
        return getAllInternal().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public Developer update(Developer developer) {
        List<Developer> allDevelopers = getAllInternal().stream()
                .peek(s -> {
                    if (s.getId().equals(developer.getId())) {
                        s.setFirstName(developer.getFirstName());
                        s.setLastName(developer.getLastName());
                        s.setSkills(developer.getSkills());
                    }
                 })
                .collect(Collectors.toList());
        writeSkillToFile(allDevelopers);
        return developer;
    }

    public void deleteById(Long id) {
        List<Developer> allDevelopers = getAllInternal();
        allDevelopers.removeIf(s -> s.getId().equals(id));
        writeSkillToFile(allDevelopers);
    }

    public Developer save(Developer developerToSave) {

        List<Developer> allDevelopers = getAllInternal();
        Developer addId = new Developer(generateId(), developerToSave.getFirstName(), developerToSave.getLastName(),
                developerToSave.getSkills());
        allDevelopers.add(addId);
        writeSkillToFile(allDevelopers);
        return addId;
    }


    private void writeSkillToFile(List<Developer> developers)  {
        BufferedWriter writerFile = null;
        Gson gson = new Gson();
        String json  = gson.toJson(developers);
        try {
            writerFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writerFile.write(json);
            writerFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Long generateId() {
        return getAllInternal().stream().map(Developer::getId).max(Comparator.comparing(aLong -> aLong)).orElse(0L) + 1;
    }
    public Developer getByName(String firstName, String lastName) {
        return getAllInternal().stream().filter(s -> s.getFirstName().equals(firstName)).filter(s -> s.getLastName().equals(lastName)).findFirst().orElse(null);
    }
    private List<Developer> getAllInternal() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Developer> developerList = new ArrayList<>();
            Developer[] developers;
            Gson gson = new Gson();
            developers  = gson.fromJson(reader, Developer[].class);
            Collections.addAll(developerList, developers);
            return developerList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
