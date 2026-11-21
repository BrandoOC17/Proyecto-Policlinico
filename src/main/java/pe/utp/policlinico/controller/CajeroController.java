package pe.utp.policlinico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.service.PagoService;

@Controller
@RequestMapping("/cajero")
public class CajeroController {

    private final PagoService pagoService;

    public CajeroController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/pendientes")
    public String pendientes(Model model) {
        model.addAttribute("citas", pagoService.citasPendientesDePago());
        return "cajero/pendientes";
    }

    // ---- CU11 Registrar pago ----
    @GetMapping("/pago/{idCita}")
    public String pagoForm(@PathVariable Integer idCita, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("cita", pagoService.verificarCita(idCita));
            return "cajero/pago";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cajero/pendientes";
        }
    }

    @PostMapping("/pago")
    public String registrarPago(@SessionAttribute("usuario") UsuarioSesion u, @RequestParam Integer idCita,
                                @RequestParam String formaPago, RedirectAttributes ra) {
        try {
            pagoService.registrarPago(idCita, formaPago, u.id());
            ra.addFlashAttribute("mensaje", "Pago registrado. Imprime el comprobante para el paciente.");
            return "redirect:/cajero/comprobante/" + idCita;
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cajero/pendientes";
        }
    }

    // ---- CU12 Emitir comprobante ----
    @GetMapping("/comprobante/{idCita}")
    public String comprobante(@PathVariable Integer idCita, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("comprobante", pagoService.comprobanteDeCita(idCita));
            return "cajero/comprobante";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cajero/pendientes";
        }
    }
}
