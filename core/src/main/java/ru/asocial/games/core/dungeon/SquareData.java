package ru.asocial.games.core.dungeon;

public enum SquareData {
    OPEN, CLOSED, G_OPEN , G_CLOSED , //GUARANTEED-OPEN AND GUARANTEED-CLOSED
    NJ_OPEN , NJ_CLOSED , NJ_G_OPEN , NJ_G_CLOSED , //NJ = non-join, these cannot be joined br Builders with others of their own kind
    IR_OPEN , IT_OPEN , IA_OPEN ,        //inside-room, open; inside-tunnel, open; inside anteroom, open
    H_DOOR , V_DOOR ,   //horizontal door, varies over y-axis , vertical door, over x-axis(up and down)
    MOB1 , MOB2 , MOB3 ,   //MOBs of different level            - higher is better
    TREAS1 , TREAS2 , TREAS3 ,  //treasure of different value
    COLUMN;

}
