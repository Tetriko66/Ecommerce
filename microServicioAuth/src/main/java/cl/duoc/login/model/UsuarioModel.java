package cl.duoc.login.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Indica que esta clase representa una tabla en la BD
@Entity

// Indica el nombre exacto de la tabla
@Table(name = "usuario")

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor vacío
@NoArgsConstructor

// Genera constructor con todos los atributos
@AllArgsConstructor
public class UsuarioModel {
    // Indica que este atributo es la clave primaria
    @Id

    // Indica que el ID se generará automáticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Mapea este atributo con la columna id_usuario
    @Column(name = "id_usuario")
    private Long idUsuario;

    // Mapea el username y exige que sea único y obligatorio
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // Mapea el password y exige que sea obligatorio
    @Column(name = "password", nullable = false)
    private String password;

    // Mapea el rol del usuario
    @Column(name = "role", nullable = false)
    private String role;

    // Permite activar o desactivar un usuario
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
}
