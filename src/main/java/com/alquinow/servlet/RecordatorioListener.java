package com.alquinow.servlet; // O el paquete donde lo hayas creado

import com.alquinow.dao.ReservaDAO;
import com.alquinow.modelo.Reserva;
import com.alquinow.util.EmailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class RecordatorioListener implements ServletContextListener {
    
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Se ejecuta al arrancar y luego una vez por día (cada 24 horas)
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Iniciando envío automático de recordatorios de reseña...");
            
            ReservaDAO reservaDAO = new ReservaDAO();
            List<Reserva> finalizadasHoy = reservaDAO.obtenerReservasFinalizadasHoy();
            
            for (Reserva reserva : finalizadasHoy) {
                if (reserva.getCorreoComprador() != null && !reserva.getCorreoComprador().isEmpty()) {
                    boolean enviado = EmailService.enviarRecordatorio(reserva.getCorreoComprador());
                    if (enviado) {
                        System.out.println("✅ Recordatorio enviado a: " + reserva.getCorreoComprador());
                    } else {
                        System.out.println("❌ Error al enviar a: " + reserva.getCorreoComprador());
                    }
                }
            }
            
        }, 0, 24, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow(); // Apaga el temporizador al detener el proyecto
        }
    }
}