package com.example.demo.controller;

import com.example.demo.dto.PublicacionDTO;
import com.example.demo.model.Publicacion;
import com.example.demo.model.Usuario;
import com.example.demo.service.PublicacionService;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;


    @GetMapping("/")
    public ResponseEntity<List<PublicacionDTO>> obtenerPublicaciones() {
        List<PublicacionDTO> publicaciones = publicacionService.obtenerPublicaciones()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerPublicacionesDeUsuario(@PathVariable String username) {
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            List<PublicacionDTO> publicacionesDTO = publicacionService.obtenerPublicacionesDeUsuario(usuario.get())
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(publicacionesDTO);
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }


    @GetMapping("/seguidos")
    public ResponseEntity<?> obtenerPublicacionesSeguidos(Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            List<PublicacionDTO> publicacionesDTO = publicacionService.obtenerPublicacionesDeSeguidos(usuario.get())
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(publicacionesDTO);
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> insertarPublicacion(@RequestBody Publicacion publicacion, Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            publicacion.setAutor(usuario.get());
            publicacion.setFechaCreacion(LocalDateTime.now());
            publicacionService.guardarPublicacion(publicacion);
            return ResponseEntity.ok("Publicación creada exitosamente");
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> editarPublicacion(@PathVariable Long id, @RequestBody String nuevoTexto, Authentication authentication) {
        return publicacionService.editarPublicacion(id, nuevoTexto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarPublicacion(@PathVariable Long id, Authentication authentication) {
        return publicacionService.borrarPublicacion(id, authentication.getName());
    }
    private PublicacionDTO convertirADTO(Publicacion publicacion) {
        PublicacionDTO dto = new PublicacionDTO();
        dto.setTexto(publicacion.getTexto());
        dto.setAutorUsername(publicacion.getAutor().getUsername());
        dto.setFechaCreacion(publicacion.getFechaCreacion());
        return dto;
    }
}
