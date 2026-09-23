package com.Fullstack.Carro.Repository;

import com.Fullstack.Carro.Model.ModelCarro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryCarro extends JpaRepository<ModelCarro, Long> {
}
