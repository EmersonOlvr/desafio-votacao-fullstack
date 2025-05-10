import React from "react";

interface CustomIconProps {
    iconClasses?: string,
}
const CustomIcon: React.FC<CustomIconProps> = ({ iconClasses }) => {
    return (
        <i
            className={iconClasses}
            style={{
                fontSize: "1.289rem",
                position: "relative",
                top: "2px",
                color: "currentColor",
            }}
        />
    );
}

export default CustomIcon;