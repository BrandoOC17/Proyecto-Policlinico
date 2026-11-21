/* Combos dependientes de la reserva de citas (CU04 + CU05) */
document.addEventListener('DOMContentLoaded', () => {
    const selEsp = document.getElementById('idEspecialidad');
    const selProf = document.getElementById('idProfesional');
    const selHor = document.getElementById('idHorario');
    const tarifa = document.getElementById('tarifa');
    const btn = document.getElementById('btnReservar');
    if (!selEsp) return;

    const vaciar = (sel, texto) => {
        sel.innerHTML = '';
        sel.add(new Option(texto, ''));
        sel.disabled = true;
    };

    const fechaLegible = (iso) => {
        const [y, m, d] = iso.split('-');
        return `${d}/${m}/${y}`;
    };

    const nombreDia = (iso) => {
        const dias = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
        const [y, m, d] = iso.split('-').map(Number);
        return dias[new Date(y, m - 1, d).getDay()];
    };

    const validar = () => { btn.disabled = !(selEsp.value && selProf.value && selHor.value); };

    selEsp.addEventListener('change', async () => {
        vaciar(selProf, 'Seleccione un profesional');
        vaciar(selHor, 'Seleccione un horario');
        const op = selEsp.options[selEsp.selectedIndex];
        tarifa.textContent = op.dataset.tarifa ? `S/ ${Number(op.dataset.tarifa).toFixed(2)}` : '—';
        validar();
        if (!selEsp.value) return;

        const resp = await fetch(`/api/profesionales?especialidad=${selEsp.value}`);
        const lista = await resp.json();
        if (lista.length === 0) {
            selProf.options[0].text = 'No hay profesionales en esta especialidad';
            return;
        }
        selProf.options[0].text = 'Seleccione un profesional';
        lista.forEach(p => selProf.add(new Option(p.nombreCompleto, p.id)));
        selProf.disabled = false;
    });

    selProf.addEventListener('change', async () => {
        vaciar(selHor, 'Seleccione un horario');
        validar();
        if (!selProf.value) return;

        const resp = await fetch(`/api/horarios?profesional=${selProf.value}`);
        const lista = await resp.json();
        if (lista.length === 0) {
            selHor.options[0].text = 'Sin horarios disponibles';
            return;
        }
        selHor.options[0].text = 'Seleccione un horario';
        lista.forEach(h => selHor.add(new Option(
            `${nombreDia(h.fecha)} ${fechaLegible(h.fecha)} - ${h.hora.substring(0, 5)}`, h.id)));
        selHor.disabled = false;
    });

    selHor.addEventListener('change', validar);
    validar();
});
