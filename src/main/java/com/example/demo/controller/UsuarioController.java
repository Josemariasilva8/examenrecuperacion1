package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ✅ (POST) Registro de usuario (público)
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuario));
    }

    // ✅ (GET) Buscar usuario por username (público)
    @GetMapping("/{username}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable String username) {
        return usuarioService.buscarPorUsername(username)
                .map(usuario -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setUsername(usuario.getUsername());
                    dto.setEmail(usuario.getEmail());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ (POST) Login de usuario (público)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(loginRequest.getUsername());

        if (usuario.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), usuario.get().getPassword())) {
            String token = jwtUtil.generateToken(usuario.get().getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    // ✅ (PATCH) Editar solo el nombre de usuario (privado)
    @PatchMapping("/editar-nombre")
    public ResponseEntity<?> editarNombreUsuario(@RequestParam String nuevoNombre, Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            Usuario user = usuario.get();
            user.setUsername(nuevoNombre);
            usuarioService.guardarUsuario(user);
            return ResponseEntity.ok("Nombre de usuario actualizado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // ✅ (GET) Obtener todos los usuarios que sigue un usuario (privado)
    @GetMapping("/siguiendo")
    public ResponseEntity<?> obtenerUsuariosSeguidos(Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get().getSeguidores());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // ✅ (GET) Obtener todos los usuarios que siguen a un usuario (privado)
    @GetMapping("/seguidores")
    public ResponseEntity<?> obtenerSeguidores(Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get().getSeguidos());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}
