import React from 'react';
import { Box, type BoxProps, Button, type ButtonProps, Tooltip, Typography, type TypographyProps } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';

export interface CrudHeaderProps {
    titleIcon?: React.ReactNode,
    title: React.ReactNode,
    TitleProps?: TypographyProps,
    BoxProps?: BoxProps,

    addButtonTitle?: string,
    AddButtonProps?: ButtonProps,
}
const CrudHeader: React.FC<CrudHeaderProps> = ({ titleIcon: Icon, title, TitleProps, BoxProps, addButtonTitle, AddButtonProps }) => {

    const [isButtonWrapped, setIsButtonWrapped] = React.useState(false);
    const containerRef = React.useRef<HTMLDivElement>(null);
    const titleRef = React.useRef<HTMLDivElement>(null);
    const buttonRef = React.useRef<HTMLButtonElement>(null);

    const checkButtonWrap = () => {
        if (containerRef.current && titleRef.current && buttonRef.current) {
            const containerWidth = containerRef.current.offsetWidth;
            const titleWidth = titleRef.current.offsetWidth;
            const buttonWidth = buttonRef.current.offsetWidth;
            setIsButtonWrapped(containerWidth < titleWidth + buttonWidth);
        }
    };

    React.useEffect(() => {
        checkButtonWrap();
        window.addEventListener('resize', checkButtonWrap);

        return () => {
            window.removeEventListener('resize', checkButtonWrap);
        };
    }, []);

    return (
        <Box
            ref={containerRef}
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            flexWrap="wrap"
            marginBottom={2}
            {...BoxProps}
        >
            <Typography
                variant="h6"
                sx={{ flexGrow: 1 }}
                {...TitleProps}
                ref={titleRef}
            >
                {Icon && (
                    <Typography component="span" fontSize="1.2rem" sx={{ position: "relative", top: "4px", mr: 1, color: "GrayText" }}>
                        {Icon}
                    </Typography>
                )}
                {title}
            </Typography>

            <div style={{
                visibility: !(isButtonWrapped) ? 'hidden' : 'unset',
                maxHeight: !(isButtonWrapped) ? '0px' : 'unset',
            }}>
                <Tooltip title={addButtonTitle ?? "Adicionar"}>
                    <span>
                        <Button
                            color="primary"
                            variant="outlined"
                            sx={{ borderRadius: '50%', minWidth: '36.5px', width: '36.5px', height: '36.5px', padding: 0 }}
                            {...AddButtonProps}
                        >
                            <AddIcon fontSize="small" />
                        </Button>
                    </span>
                </Tooltip>
            </div>

            {
                (!AddButtonProps?.hidden) &&
                <div style={{
                    visibility: (isButtonWrapped) ? 'hidden' : 'unset',
                    maxHeight: (isButtonWrapped) ? '0px' : 'unset',
                }}>
                    <Button
                        ref={buttonRef}
                        color="primary"
                        variant="outlined"
                        startIcon={<AddIcon />}
                        {...AddButtonProps}
                    >
                        {addButtonTitle ?? "Adicionar"}
                    </Button>
                </div>
            }
        </Box>
    );

}

export default CrudHeader;