package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {
    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        //given
        List<Todo> todos = new ArrayList<>();
        todos.add(new Todo(1, "sample", true, 2));
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title").value("sample"));
    }

    @Test
    void getTodo() throws Exception {
        //given
        when(todoRepository.findById(1)).thenReturn(Optional.of(new Todo("TitleSample", true)));
        //when
        ResultActions result = mvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is("TitleSample")))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteOneTodo() throws Exception {
        //given
        when(todoRepository.findById(1)).thenReturn(Optional.of(new Todo("TitleSample", true)));
        //when
        ResultActions result = mvc.perform(delete("/todos/1"));
        //then
        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void saveTodo() throws Exception {
        //when
        ResultActions result = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Todo("chloe's todo", true))));
        //then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title", is("chloe's todo")))
                .andExpect(jsonPath("$.completed", is(true)));
    }


    @Test
    void updateTodo() throws Exception {
        //given
        Todo todo = new Todo("TitleSample", true);
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));
        //when
        ResultActions result = mvc.perform(patch("/todos/1", todo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is("TitleSample")))
                .andExpect(jsonPath("$.completed", is(true)));
    }
}