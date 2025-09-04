import React, { useState } from 'react';
import axios from 'axios';

interface AddCommentProps {
    glpiTicketId: string;
}
const AddComment: React.FC<AddCommentProps> = ({ glpiTicketId }) => {

    const [showForm, setShowForm] = useState(false);

    const [comment, setComment] = useState('');

    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    // Maneja el envío del comentario al backend
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        e.stopPropagation();
        try {
            // Realiza la llamada POST al endpoint del backend
            await axios.post(`http://localhost:9090/api/events/${glpiTicketId}/notes`, {
                content: comment
            });
            // Si todo va bien, limpiamos el formulario y damos feedback
            setComment('');
            setShowForm(false);
            setMessage('Comentario agregado');
            setError('');
        } catch (error) {
            // En caso de error mostramos un mensaje y registramos en consola
            console.error(error);
            setError('Error al agregar comentario');
            setMessage('');
        }
    };

    const handleCancel = (e: React.MouseEvent) => {
        e.stopPropagation(); // Evita colapsar la tarjeta al cancelar
        setShowForm(false);
        setComment('');
        setMessage('');
        setError('');
    };
    // Muestra el formulario al hacer clic en el botón
    const handleShowForm = (e: React.MouseEvent) => {
        e.stopPropagation();
        setShowForm(true);
    };
    return (
        <div className="add-comment-container" onClick={(e) => e.stopPropagation()}>
            {showForm ? (
                <form className="add-comment-form" onSubmit={handleSubmit}>
                    {/* Campo para escribir el comentario */}
                    <textarea
                        className="add-comment-textarea"
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        rows={3}
                        placeholder="Escribe tu comentario..."
                    />
                    <div className="add-comment-actions">
                        <button type="submit" className="add-comment-submit">Enviar</button>
                        <button type="button" className="add-comment-cancel" onClick={handleCancel}>
                            Cancelar
                        </button>
                    </div>
                    {/* Mensajes de feedback */}
                    {message && <p className="add-comment-success">{message}</p>}
                    {error && <p className="add-comment-error">{error}</p>}
                </form>
            ) : (
                <button className="add-comment-toggle" onClick={handleShowForm}>
                    Agregar comentario
                </button>
            )}
        </div>
    );
};
export default AddComment;