import React from 'react';
import MuiPaper, { type PaperProps } from '@mui/material/Paper';
import { styled } from '@mui/material';

export interface CardProps extends PaperProps {
    children: React.ReactNode,
    border?: boolean,
}

export const Paper = styled(MuiPaper)<PaperProps>(({ }) => ({
    // backgroundColor: theme.palette.appBar.main,
    position: "relative",
    padding: 16,
    display: 'flex',
    flexDirection: 'column',
    borderRadius: 18,
    boxShadow: 'rgb(0 0 0 / 10%) 0px 9px 16px, rgb(0 0 0 / 1%) 0px 2px 2px',
}));

const Card: React.FC<CardProps> = ({ children, border, ...props }) => {

    return (
        <Paper elevation={3} {...props} sx={{ border: border ? "1px solid rgba(0, 0, 0, 0.15)" : undefined, ...props.sx }}>
            {children}
        </Paper>
    );

}

export default Card;