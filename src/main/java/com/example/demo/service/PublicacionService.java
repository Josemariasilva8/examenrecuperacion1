package com.example.demo.service;

import com.example.demo.model.Publicacion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;


    public List<Publicacion> obtenerPublicaciones() {
        return publicacionRepository.findAll();
    }

    public List<Publicacion> obtenerPublicacionesDeUsuario(Usuario usuario) {
        return publicacionRepository.findByAutor(usuario);
    }

  
    public List<Publicacion> obtenerPublicacionesDeSeguidos(Usuario usuario) {
        return usuario.getSeguidores().stream()
                .flatMap(seguidor -> publicacionRepository.findByAutor(seguidor).stream())
                .toList();
    }


    public void guardarPublicacion(Publicacion publicacion) {
        publicacionRepository.save(publicacion);
    }


    public ResponseEntity<?> editarPublicacion(Long id, String nuevoTexto, String username) {
        Optional<Publicacion> publicacionOpt = publicacionRepository.findById(id);

        if (publicacionOpt.isPresent()) {
            Publicacion publicacion = publicacionOpt.get();
            if (!publicacion.getAutor().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes editar esta publicación");
            }
            publicacion.setTexto(nuevoTexto);
            publicacion.setFechaEdicion(LocalDateTime.now());
            publicacionRepository.save(publicacion);
            return ResponseEntity.ok("Publicación editada correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada");
    }


    public ResponseEntity<?> borrarPublicacion(Long id, String username) {
        Optional<Publicacion> publicacionOpt = publicacionRepository.findById(id);

        if (publicacionOpt.isPresent()) {
            Publicacion publicacion = publicacionOpt.get();
            if (!publicacion.getAutor().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar esta publicación");
            }
            publicacionRepository.delete(publicacion);
            return ResponseEntity.ok("Publicación eliminada correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada");
    }
}
