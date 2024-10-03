package com.tictactoe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/TicTacToeServlet")
public class TicTacToeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
//var
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        char[][] board = (char[][]) session.getAttribute("board");
        Character currentPlayer = (Character) session.getAttribute("currentPlayer");
        String winner = (String) session.getAttribute("winner");

        if (board == null) {
            board = new char[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = '-';
                }
            }
            currentPlayer = 'X';
            session.setAttribute("board", board);
            session.setAttribute("currentPlayer", currentPlayer);
        }

        if (currentPlayer == null) {
            currentPlayer = 'X';
            session.setAttribute("currentPlayer", currentPlayer);
        }

        displayGame(response, board, currentPlayer, winner);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        char[][] board = (char[][]) session.getAttribute("board");
        Character currentPlayer = (Character) session.getAttribute("currentPlayer");

        if ("restart".equals(request.getParameter("action"))) {
            session.invalidate();
            response.sendRedirect("TicTacToeServlet");
            return;
        }

        try {
            int row = Integer.parseInt(request.getParameter("row"));
            int col = Integer.parseInt(request.getParameter("col"));

            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '-') {
                board[row][col] = currentPlayer;
                String winner = checkWinner(board);
                session.setAttribute("winner", winner);
                if (winner == null) {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    session.setAttribute("currentPlayer", currentPlayer);
                }
            }

            session.setAttribute("board", board);
        } catch (NumberFormatException | NullPointerException e) {
            // Handle invalid or missing row/col parameters
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid move");
            return;
        }

        displayGame(response, board, currentPlayer, (String) session.getAttribute("winner"));
    }

    private void displayGame(HttpServletResponse response, char[][] board, char currentPlayer, String winner) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Tic-Tac-Toe</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background-color: #f0f0f0; }");
        out.println(".game-container { text-align: center; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; }");
        out.println(".board { display: inline-grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-top: 20px; }");
        out.println(".cell { width: 100px; height: 100px; font-size: 40px; font-weight: bold; border: none; background-color: #e0e0e0; cursor: pointer; }");
        out.println(".cell:hover { background-color: #d0d0d0; }");
        out.println(".X { color: blue; }");
        out.println(".O { color: red; }");
        out.println(".winner, .current-player { margin-top: 20px; font-size: 24px; font-weight: bold; }");
        out.println(".restart { margin-top: 20px; padding: 10px 20px; font-size: 18px; background-color: #4CAF50; color: white; border: none; border-radius: 5px; cursor: pointer; }");
        out.println(".restart:hover { background-color: #45a049; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='game-container'>");
        out.println("<h1>Tic-Tac-Toe</h1>");
        
        out.println("<div class='board'>");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    out.println("<form action='TicTacToeServlet' method='post' style='display:contents;'>");
                    out.println("<input type='hidden' name='row' value='" + i + "'>");
                    out.println("<input type='hidden' name='col' value='" + j + "'>");
                    out.println("<input type='submit' class='cell' value=' '>");
                    out.println("</form>");
                } else {
                    out.println("<button class='cell " + board[i][j] + "' disabled>" + board[i][j] + "</button>");
                }
            }
        }
        out.println("</div>");

        if (winner != null) {
            out.println("<div class='winner'>" + winner + "</div>");
            out.println("<form action='TicTacToeServlet' method='post'>");
            out.println("<input type='hidden' name='action' value='restart'>");
            out.println("<input type='submit' class='restart' value='Play Again'>");
            out.println("</form>");
        } else {
            out.println("<div class='current-player'>Current Player: <span class='" + currentPlayer + "'>" + currentPlayer + "</span></div>");
        }
        
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private String checkWinner(char[][] board) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return "Player " + board[i][0] + " wins!";
            }
            if (board[0][i] != '-' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return "Player " + board[0][i] + " wins!";
            }
        }
        if (board[0][0] != '-' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return "Player " + board[0][0] + " wins!";
        }
        if (board[0][2] != '-' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return "Player " + board[0][2] + " wins!";
        }
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '-') return null;
            }
        }
        return "It's a draw!";
    }
}