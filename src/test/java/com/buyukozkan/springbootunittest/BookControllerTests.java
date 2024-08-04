package com.buyukozkan.springbootunittest;

import com.buyukozkan.springbootunittest.model.Book;
import com.buyukozkan.springbootunittest.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class BookControllerTests {

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "This test should return list of books.")
    void shouldReturnListOfBooks() throws Exception {
        List<Book> books = new ArrayList<>(
            Arrays.asList(
                new Book(1, "Spring Boot @WebMvcTest 1", "Description1", "Author1"),
                new Book(2, "Spring Boot @WebMvcTest 2", "Description2", "Author2")));

        when(bookRepository.findAll()).thenReturn(books);
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(books.size()))
            .andDo(print());
    }

    @Test
    @DisplayName(value = "This test should return created book.")
    void shouldCreateBook() throws Exception {
        Book book = new Book(1, "Spring Boot @WebMvcTest", "Author", "Description");

        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andDo(print());
    }

    @Test
    @DisplayName("Should return the book for a given ID")
    void shouldReturnBook() throws Exception {
        long id = 1L;
        Book book = new Book(id, "Spring Boot @WebMvcTest", "Author", "Description");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(book.getName()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()))
                .andExpect(jsonPath("$.description").value(book.getDescription()))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return updated book")
    void shouldUpdateBook() throws Exception {
        long id = 1L;
        Book book = new Book(id, "Spring Boot @WebMvcTest", "Author", "Description");
        Book updatedBook = new Book(id, "Spring Boot @WebMvcTest Updated", "Author Updated", "Description Updated");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedBook.getName()))
                .andExpect(jsonPath("$.author").value(updatedBook.getAuthor()))
                .andExpect(jsonPath("$.description").value(updatedBook.getDescription()))
                .andDo(print());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        long id = 1L;
        Book book = new Book(id, "Spring Boot @WebMvcTest", "Author", "Description");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
