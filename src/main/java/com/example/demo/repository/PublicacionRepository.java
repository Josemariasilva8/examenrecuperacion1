package com.example.demo.repository;

import com.example.demo.model.Publicacion;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByAutor(Usuario autor);
}
