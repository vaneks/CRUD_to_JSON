package com.vaneks.crud.repository.io;

import com.google.gson.*;
import com.vaneks.crud.model.Skill;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JsonSkillRepositoryImpl implements com.vaneks.crud.repository.SkillRepository {
    private final String file = "src/main/resources/skill.json";

    private static JsonSkillRepositoryImpl jsonSkillRepository;
    public static synchronized JsonSkillRepositoryImpl getJsonSkillRepository() {
        if(jsonSkillRepository == null) {
            jsonSkillRepository = new JsonSkillRepositoryImpl();
        }
        return jsonSkillRepository;
    }

    private JsonSkillRepositoryImpl(){}

    public List<Skill> getAll() {
        return getAllInternal();
    }

    public Skill getById(Long id) {
        return getAllInternal().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public Skill update(Skill skill) {
        List<Skill> allSkills = getAllInternal().stream()
                .peek(s -> {
                    if (s.getId().equals(skill.getId()))
                        s.setName(skill.getName());
                })
                .collect(Collectors.toList());
        writeSkillToFile(allSkills);
        return skill;
    }

    public void deleteById(Long id) {
        List<Skill> allSkills = getAllInternal();
        allSkills.removeIf(s -> s.getId().equals(id));
        writeSkillToFile(allSkills);
    }

    public Skill save(Skill skillToSave) {
        List<Skill> allSkills = getAllInternal();
        Skill addId = new Skill(generateId(), skillToSave.getName());
        allSkills.add(addId);
        writeSkillToFile(allSkills);
        return addId;
    }

    private void writeSkillToFile(List<Skill> skills) {
        BufferedWriter writerFile = null;
        Gson gson = new Gson();
        String json  = gson.toJson(skills);
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
        return getAllInternal().stream().map(Skill::getId).max(Comparator.comparing(aLong -> aLong)).orElse(0L) + 1;
    }

    private List<Skill> getAllInternal() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Skill> skillList = new ArrayList<>();
            Skill[] skill;
            Gson gson = new Gson();
            skill  = gson.fromJson(reader, Skill[].class);
            Collections.addAll(skillList, skill);
            return skillList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
