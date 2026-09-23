package com.Fullstack.Pago.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Fullstack.Pago.Model.ModelPago;

public interface PagoRepository extends JpaRepository<ModelPago, Long> {
}