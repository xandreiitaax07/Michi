import requests

BASE_URL = "http://localhost:8080"
JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpbIkFETUlOSVNUUkFET1IiXSwic3ViIjoiMSIsImlhdCI6MTc3Nzc4ODc3NSwiZXhwIjoxODQwODYwNzc1fQ.QItS3nUduiC52ty2wx4MlZzTHq_N6t-QkFOg78sSdrFeWjhktrB6NCxq-CEuxIzRO9t8LItJMBK4bBXfrGzGfg"


POSTS_TO_EXECUTE = [
    {"name": "Crear convocatoria 1 - 2025", "url": f"{BASE_URL}/convocatorias", "payload": {"idConvocatoria": 0, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}},
    {"name": "Crear convocatoria 2 - 2026 (Actual)", "url": f"{BASE_URL}/convocatorias", "payload": {"idConvocatoria": 0, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}},
    {"name": "Materia 1: Lengua Castellana y Literatura", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Lengua Castellana y Literatura", "id": 1, "eliminada": False}},
    {"name": "Materia 2: Historia de España", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Historia de España", "id": 2, "eliminada": False}},
    {"name": "Materia 3: Inglés (Fase de Acceso)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Inglés (Fase de Acceso)", "id": 3, "eliminada": False}},
    {"name": "Materia 4: Francés (Fase de Acceso)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Francés (Fase de Acceso)", "id": 4, "eliminada": False}},
    {"name": "Materia 5: Alemán (Fase de Acceso)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Alemán (Fase de Acceso)", "id": 5, "eliminada": False}},
    {"name": "Materia 6: Italiano (Fase de Acceso)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Italiano (Fase de Acceso)", "id": 6, "eliminada": False}},
    {"name": "Materia 7: Portugués (Fase de Acceso)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Portugués (Fase de Acceso)", "id": 7, "eliminada": False}},
    {"name": "Materia 8: Inglés (Fase de Admisión)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Inglés (Fase de Admisión)", "id": 8, "eliminada": False}},
    {"name": "Materia 9: Francés (Fase de Admisión)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Francés (Fase de Admisión)", "id": 9, "eliminada": False}},
    {"name": "Materia 10: Alemán (Fase de Admisión)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Alemán (Fase de Admisión)", "id": 10, "eliminada": False}},
    {"name": "Materia 11: Italiano (Fase de Admisión)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Italiano (Fase de Admisión)", "id": 11, "eliminada": False}},
    {"name": "Materia 12: Portugués (Fase de Admisión)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Portugués (Fase de Admisión)", "id": 12, "eliminada": False}},
    {"name": "Materia 13: Fundamentos del Arte II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Fundamentos del Arte II", "id": 13, "eliminada": False}},
    {"name": "Materia 14: Latín II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Latín II", "id": 14, "eliminada": False}},
    {"name": "Materia 15: Matemáticas II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Matemáticas II", "id": 15, "eliminada": False}},
    {"name": "Materia 16: Matemáticas Aplicadas a las CCSS", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Matemáticas Aplicadas a las CCSS", "id": 16, "eliminada": False}},
    {"name": "Materia 17: Artes Escénicas", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Artes Escénicas", "id": 17, "eliminada": False}},
    {"name": "Materia 18: Biología", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Biología", "id": 18, "eliminada": False}},
    {"name": "Materia 19: Cultura Audiovisual II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Cultura Audiovisual II", "id": 19, "eliminada": False}},
    {"name": "Materia 20: Dibujo Técnico II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Dibujo Técnico II", "id": 20, "eliminada": False}},
    {"name": "Materia 21: Diseño", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Diseño", "id": 21, "eliminada": False}},
    {"name": "Materia 22: Economía de la Empresa", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Economía de la Empresa", "id": 22, "eliminada": False}},
    {"name": "Materia 23: Física", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Física", "id": 23, "eliminada": False}},
    {"name": "Materia 24: Fundamentos del Arte II (Bis)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Fundamentos del Arte II", "id": 24, "eliminada": False}},
    {"name": "Materia 25: Geografía", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Geografía", "id": 25, "eliminada": False}},
    {"name": "Materia 26: Geología", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Geología", "id": 26, "eliminada": False}},
    {"name": "Materia 27: Griego II", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Griego II", "id": 27, "eliminada": False}},
    {"name": "Materia 28: Historia de la Filosofía", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Historia de la Filosofía", "id": 28, "eliminada": False}},
    {"name": "Materia 29: Historia del Arte", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Historia del Arte", "id": 29, "eliminada": False}},
    {"name": "Materia 30: Matemáticas II (Bis)", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Matemáticas II", "id": 30, "eliminada": False}},
    {"name": "Materia 31: Química", "url": f"{BASE_URL}/materias", "payload": {"nombre": "Química", "id": 31, "eliminada": False}},
    {
        "name": "Slot 1: Julio 2025 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 1, "inicio": "2025-07-03T10:30:00.000Z", "fin": "2025-07-03T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 2: Julio 2025 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 2, "inicio": "2025-07-03T13:00:00.000Z", "fin": "2025-07-03T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 3: Julio 2025 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 3, "inicio": "2025-07-03T15:30:00.000Z", "fin": "2025-07-03T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 4: Julio 2025 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 4, "inicio": "2025-07-04T10:30:00.000Z", "fin": "2025-07-04T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 5: Julio 2025 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 5, "inicio": "2025-07-04T13:00:00.000Z", "fin": "2025-07-04T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 6: Julio 2025 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 6, "inicio": "2025-07-04T15:30:00.000Z", "fin": "2025-07-04T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 7: Julio 2025 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 7, "inicio": "2025-07-05T10:30:00.000Z", "fin": "2025-07-05T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 8: Julio 2025 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 8, "inicio": "2025-07-05T13:00:00.000Z", "fin": "2025-07-05T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 9: Julio 2025 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 9, "inicio": "2025-07-05T15:30:00.000Z", "fin": "2025-07-05T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 1, "nombre": "Julio 2025", "fechaInicio": "2025-07-03T00:00:00.000Z", "fechaFin": "2025-07-05T00:00:00.000Z"}}
    },
    {
        "name": "Slot 10: Junio 2026 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 10, "inicio": "2026-06-02T10:30:00.000Z", "fin": "2026-06-02T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 11: Junio 2026 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 11, "inicio": "2026-06-02T13:00:00.000Z", "fin": "2026-06-02T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 12: Junio 2026 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 12, "inicio": "2026-06-02T15:30:00.000Z", "fin": "2026-06-02T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 13: Junio 2026 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 13, "inicio": "2026-06-03T10:30:00.000Z", "fin": "2026-06-03T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 14: Junio 2026 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 14, "inicio": "2026-06-03T13:00:00.000Z", "fin": "2026-06-03T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 15: Junio 2026 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 15, "inicio": "2026-06-03T15:30:00.000Z", "fin": "2026-06-03T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 16: Junio 2026 (10:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 16, "inicio": "2026-06-04T10:30:00.000Z", "fin": "2026-06-04T12:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 17: Junio 2026 (13:00)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 17, "inicio": "2026-06-04T13:00:00.000Z", "fin": "2026-06-04T14:30:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {
        "name": "Slot 18: Junio 2026 (15:30)",
        "url": f"{BASE_URL}/slots",
        "payload": {"id": 18, "inicio": "2026-06-04T15:30:00.000Z", "fin": "2026-06-04T17:00:00.000Z", "eliminado": False, "convocatoria": {"idConvocatoria": 2, "nombre": "Junio 2026", "fechaInicio": "2026-06-02T00:00:00.000Z", "fechaFin": "2026-06-04T00:00:00.000Z"}}
    },
    {"name": "Relación 1: Lengua Castellana y Literatura", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 1, "idMateria": 1}},
    {"name": "Relación 2: Historia de España", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 2, "idMateria": 2}},
    {"name": "Relación 3: Inglés (Fase de Acceso)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 3, "idMateria": 3}},
    {"name": "Relación 4: Francés (Fase de Acceso)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 3, "idMateria": 4}},
    {"name": "Relación 5: Alemán (Fase de Acceso)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 3, "idMateria": 5}},
    {"name": "Relación 6: Italiano (Fase de Acceso)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 3, "idMateria": 6}},
    {"name": "Relación 7: Portugués (Fase de Acceso)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 3, "idMateria": 7}},
    {"name": "Relación 8: Fundamentos del Arte II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 4, "idMateria": 24}},
    {"name": "Relación 9: Latín II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 4, "idMateria": 14}},
    {"name": "Relación 10: Matemáticas II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 4, "idMateria": 30}},
    {"name": "Relación 11: Griego II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 5, "idMateria": 27}},
    {"name": "Relación 12: Matemáticas Aplicadas a las CCSS", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 5, "idMateria": 16}},
    {"name": "Relación 13: Física", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 6, "idMateria": 23}},
    {"name": "Relación 14: Historia de la Filosofía", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 6, "idMateria": 28}},
    {"name": "Relación 15: Dibujo Técnico II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 7, "idMateria": 20}},
    {"name": "Relación 16: Cultura Audiovisual II", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 7, "idMateria": 19}},
    {"name": "Relación 17: Biología", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 7, "idMateria": 18}},
    {"name": "Relación 18: Inglés (Fase de Admisión)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 8}},
    {"name": "Relación 19: Francés (Fase de Admisión)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 9}},
    {"name": "Relación 20: Alemán (Fase de Admisión)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 10}},
    {"name": "Relación 21: Italiano (Fase de Admisión)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 11}},
    {"name": "Relación 22: Portugués (Fase de Admisión)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 12}},
    {"name": "Relación 23: Diseño", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 21}},
    {"name": "Relación 24: Geografía", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 25}},
    {"name": "Relación 25: Química", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 8, "idMateria": 31}},
    {"name": "Relación 26: Artes Escénicas", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 9, "idMateria": 17}},
    {"name": "Relación 27: Economía de la Empresa", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 9, "idMateria": 22}},
    {"name": "Relación 28: Geología", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 9, "idMateria": 26}},
    {"name": "Relación 29: Historia del Arte", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 9, "idMateria": 29}},
    {"name": "Relación 30: Lengua Castellana y Literatura (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 10, "idMateria": 1}},
    {"name": "Relación 31: Historia de España (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 11, "idMateria": 2}},
    {"name": "Relación 32: Inglés (Fase de Acceso) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 12, "idMateria": 3}},
    {"name": "Relación 33: Francés (Fase de Acceso) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 12, "idMateria": 4}},
    {"name": "Relación 34: Alemán (Fase de Acceso) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 12, "idMateria": 5}},
    {"name": "Relación 35: Italiano (Fase de Acceso) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 12, "idMateria": 6}},
    {"name": "Relación 36: Portugués (Fase de Acceso) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 12, "idMateria": 7}},
    {"name": "Relación 37: Fundamentos del Arte II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 13, "idMateria": 24}},
    {"name": "Relación 38: Latín II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 13, "idMateria": 14}},
    {"name": "Relación 39: Matemáticas II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 13, "idMateria": 30}},
    {"name": "Relación 40: Griego II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 14, "idMateria": 27}},
    {"name": "Relación 41: Matemáticas Aplicadas a las CCSS (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 14, "idMateria": 16}},
    {"name": "Relación 42: Física (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 15, "idMateria": 23}},
    {"name": "Relación 43: Historia de la Filosofía (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 15, "idMateria": 28}},
    {"name": "Relación 44: Dibujo Técnico II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 16, "idMateria": 20}},
    {"name": "Relación 45: Cultura Audiovisual II (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 16, "idMateria": 19}},
    {"name": "Relación 46: Biología (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 16, "idMateria": 18}},
    {"name": "Relación 47: Inglés (Fase de Admisión) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 8}},
    {"name": "Relación 48: Francés (Fase de Admisión) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 9}},
    {"name": "Relación 49: Alemán (Fase de Admisión) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 10}},
    {"name": "Relación 50: Italiano (Fase de Admisión) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 11}},
    {"name": "Relación 51: Portugués (Fase de Admisión) (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 12}},
    {"name": "Relación 52: Diseño (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 21}},
    {"name": "Relación 53: Geografía (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 25}},
    {"name": "Relación 54: Química (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 17, "idMateria": 31}},
    {"name": "Relación 55: Artes Escénicas (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 18, "idMateria": 17}},
    {"name": "Relación 56: Economía de la Empresa (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 18, "idMateria": 22}},
    {"name": "Relación 57: Geología (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 18, "idMateria": 26}},
    {"name": "Relación 58: Historia del Arte (2026)", "url": f"{BASE_URL}/pruebas", "payload": {"idSlot": 18, "idMateria": 29}}
]

def execute_posts():
    headers = {
        "Authorization": f"Bearer {JWT_TOKEN}",
        "Content-Type": "application/json"
    }

    print("Ejecutando POSTs...\n")

    for post in POSTS_TO_EXECUTE:
        print(f"⏳ Ejecutando: {post['name']}...")
        try:
            response = requests.post(post["url"], json=post["payload"], headers=headers)
            print(f"✅ Éxito ({response.status_code}):", response.json())
        except requests.exceptions.RequestException as e:
            # Intenta mostrar el error del servidor si existe, si no el error genérico
            server_error = e.response.json() if e.response else e
            print(f"❌ Error en '{post['name']}': {server_error}")
        print("-"*40)

if __name__ == "__main__":
    execute_posts()